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
import nl.knaw.dans.lib.bagpack.mappings.metadata.Publication;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.Datacite;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Publications extends Base {

    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        return toPublications(resource, getPublications(deposit.getDdm()));
    }

    static List<Publication> getPublications(Document document) {
        // CIT017
        var idType = getIdTypeNamespace(document);

        // identifier might have dcterms or dc as prefix, query both
        var queries = Set.of("dcterms", "dc").stream().map(prefix ->
                String.format("/ddm:DDM/ddm:dcmiMetadata/%s:identifier[" +
                    "@xsi:type = '%s:ISSN' or @xsi:type = '%s:ISBN'" +
                    "]", prefix, idType, idType))
            .toArray(String[]::new);

        return XPathEvaluator.nodes(document, queries)
            .map(node -> {
                var idTypeValue = node.getAttributes()
                    .getNamedItem("xsi:type").getTextContent()
                    .trim()
                    .replaceAll("id-type:", "");

                return Publication.builder()
                    .idType(idTypeValue)
                    .idNumber(node.getTextContent().trim())
                    .build();
            })
            .collect(Collectors.toList());
    }

    static List<Statement> toPublications(Resource resource, Collection<Publication> publications) {
        return toComplexTerms(resource, DCTerms.isReferencedBy, publications, (element, publication) -> {
            if (publication.getIdNumber() != null) {
                element.addProperty(Datacite.resourceIdentifier, publication.getIdNumber());
            }

            if (publication.getIdType() != null) {
                element.addProperty(Datacite.resourceIdentifierScheme, publication.getIdType());
            }
        });
    }
}
