/*
 * Copyright (C) 2025 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.lib.bagpack.mappings;

import nl.knaw.dans.lib.bagpack.deposit.Deposit;
import nl.knaw.dans.lib.bagpack.mappings.metadata.Funder;
import nl.knaw.dans.lib.bagpack.mappings.metadata.GrantNumber;
import nl.knaw.dans.lib.bagpack.mappings.metadata.NWOGrantNumber;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DVCitation;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.SchemaDO;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GrantNumbers extends Base {
    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        var grantNumbers = new ArrayList<GrantNumber>();

        grantNumbers.addAll(getGrantNumbers(deposit.getDdm()));
        grantNumbers.addAll(getFunders(deposit.getDdm()));

        return toGrantNumbers(resource, grantNumbers);
    }

    // CIT022
    static List<GrantNumber> getFunders(Document document) {
        return XPathEvaluator.nodes(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:funding")
            .map(node -> Funder.builder()
                .funderName(getFirstValue(node, "ddm:funderName"))
                .fundingProgramme(getFirstValue(node, "ddm:fundingProgramme"))
                .awardNumber(getFirstValue(node, "ddm:awardNumber"))
                .awardTitle(getFirstValue(node, "ddm:awardTitle"))
                .build())
            .collect(Collectors.toList());

    }

    // CIT023
    static List<GrantNumber> getGrantNumbers(Document document) {
        var idType = getIdTypeNamespace(document);

        return XPathEvaluator.strings(document,
                String.format(
                    "/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier[@xsi:type = '%s:NWO-PROJECTNR']", idType)
            )
            .map(value -> NWOGrantNumber.builder()
                .value(value)
                .build())
            .collect(Collectors.toList());
    }

    static List<Statement> toGrantNumbers(Resource resource, Collection<GrantNumber> grantNumbers) {
        return toComplexTerms(resource, SchemaDO.sponsor, grantNumbers, (element, grantNumber) -> {
            if (grantNumber.getAgency() != null) {
                element.addProperty(DVCitation.grantNumberAgency, grantNumber.getAgency());
            }
            if (grantNumber.getValue() != null) {
                element.addProperty(DVCitation.grantNumberValue, grantNumber.getValue());
            }
        });
    }
}
