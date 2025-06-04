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
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlternativeTitles {
    // CIT002
    public static Optional<Statement> toRDF(Resource resource, Deposit deposit) {
        return toAlternativeTitle(resource, getAlternativeTitles(deposit.getDdm()));
    }

    static List<String> getAlternativeTitles(Document ddm) {
        return XPathEvaluator.strings(ddm,
                "/ddm:DDM/ddm:dcmiMetadata/dcterms:title",
                "/ddm:DDM/ddm:dcmiMetadata/dc:title",
                "/ddm:DDM/ddm:dcmiMetadata/dcterms:alternative")
            .map(String::trim)
            .collect(Collectors.toList());
    }

    static Optional<Statement> toAlternativeTitle(Resource resource, Collection<String> titles) {
        if (titles == null) {
            return Optional.empty();
        }

        var model = resource.getModel();

        return titles.stream()
            .map(title -> model.createStatement(
                resource,
                DCTerms.alternative,
                model.createLiteral(title)
            ))
            .findFirst();
    }
}
