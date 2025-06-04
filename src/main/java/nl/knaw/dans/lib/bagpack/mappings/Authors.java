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
import nl.knaw.dans.lib.bagpack.mappings.metadata.DatasetAuthor;
import nl.knaw.dans.lib.bagpack.mappings.metadata.DatasetCreator;
import nl.knaw.dans.lib.bagpack.mappings.metadata.DatasetOrganization;
import nl.knaw.dans.lib.bagpack.mappings.metadata.DatasetRelation;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DVCitation;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.Datacite;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Authors extends Base {
    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        var results = new ArrayList<DatasetRelation>();
        var ddm = deposit.getDdm();

        // CIT005
        results.addAll(getCreators(ddm));

        // CIT006
        results.addAll(getAuthors(ddm));

        // CIT007
        results.addAll(getOrganizations(ddm));

        return toAuthors(resource, results);
    }

    static List<Statement> toAuthors(Resource resource, Collection<DatasetRelation> authors) {
        return toComplexTerms(resource, DVCitation.author, authors, (element, author) -> {
            element.addProperty(DVCitation.authorName, author.getDisplayName());

            if (author.getAffiliation() != null) {
                element.addProperty(DVCitation.authorAffiliation, author.getAffiliation());
            }

            var identifier = author.getIdentifier();

            if (identifier != null) {
                if (identifier.getScheme() != null) {
                    element.addProperty(Datacite.agentIdentifierScheme, identifier.getScheme());
                }

                if (identifier.getValue() != null) {
                    element.addProperty(Datacite.agentIdentifier, identifier.getValue());
                }
            }
        });
    }

    static List<DatasetAuthor> getAuthors(Document ddm) {
        return XPathEvaluator.nodes(ddm,
                "/ddm:DDM/ddm:profile/dcx-dai:creatorDetails/dcx-dai:author")
            .map(Authors::parseAuthor)
            .collect(Collectors.toList());
    }

    static DatasetAuthor parseAuthor(Node node) {
        return DatasetAuthor.builder()
            .titles(getFirstValue(node, "dcx-dai:titles"))
            .initials(getFirstValue(node, "dcx-dai:initials"))
            .insertions(getFirstValue(node, "dcx-dai:insertions"))
            .surname(getFirstValue(node, "dcx-dai:surname"))
            .dai(getDAI(node))
            .isni(getISNI(node))
            .orcid(getORCID(node))
            .role(getFirstValue(node, "dcx-dai:role"))
            .affiliation(getFirstValue(node, "dcx-dai:organization/dcx-dai:name"))
            .build();
    }

    static List<DatasetCreator> getCreators(Document ddm) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:profile/dc:creator")
            .map(String::trim)
            .map(author -> DatasetCreator.builder()
                .name(author)
                .build()
            )
            .collect(Collectors.toList());
    }

    static List<DatasetOrganization> getOrganizations(Document ddm) {
        return XPathEvaluator.nodes(ddm,
                "/ddm:DDM/ddm:profile/dcx-dai:creatorDetails/dcx-dai:organization")
            .map(node -> DatasetOrganization.builder()
                .name(getFirstValue(node, "dcx-dai:name"))
                .isni(getISNI(node))
                .viaf(getVIAF(node))
                .build())
            .collect(Collectors.toList());
    }
}