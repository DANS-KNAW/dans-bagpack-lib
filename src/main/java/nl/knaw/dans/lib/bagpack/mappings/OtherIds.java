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
import nl.knaw.dans.lib.bagpack.mappings.Base;
import nl.knaw.dans.lib.bagpack.mappings.metadata.OtherId;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DVCitation;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OtherIds extends Base {

    // CIT003, CIT004
    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        return toOtherIds(resource, getOtherIds(deposit.getDdm(), deposit.getMetadataValue("Has-Organizational-Identifier")));
    }

    static List<OtherId> getOtherIds(Document document, List<String> organizationIdentifiers) {
        var results = new ArrayList<OtherId>();

        // CIT003, data from bag
        organizationIdentifiers
            .stream()
            .filter(value -> {
                var parts = value.split(":", 2);
                return parts.length == 2 && StringUtils.isNotBlank(parts[0]) && StringUtils.isNotBlank(parts[1]);
            })
            .map(value -> {
                var parts = value.split(":", 2);
                return OtherId.builder()
                    .agency(parts[0])
                    .value(parts[1])
                    .build();
            })
            .findFirst()
            .ifPresent(results::add);

        // CIT004, data from ddm
        XPathEvaluator.strings(document,
                "/ddm:DDM/ddm:dcmiMetadata/ddm:identifier[not(@xsi:type)]",
                "/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier[not(@xsi:type)]")
            .map(identifier -> OtherId.builder()
                .value(identifier)
                .build()
            )
            .forEach(results::add);

        return results;
    }

    static List<Statement> toOtherIds(Resource resource, Collection<OtherId> ids) {
        return toComplexTerms(resource, DVCitation.otherId, ids, (element, id) -> {
            if (id.getAgency() != null) {
                element.addProperty(DVCitation.otherIdAgency, id.getAgency());
            }

            element.addProperty(DVCitation.otherIdValue, id.getValue());
        });
    }
}
