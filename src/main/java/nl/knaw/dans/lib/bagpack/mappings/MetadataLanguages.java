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
import nl.knaw.dans.lib.bagpack.deposit.LanguageResolver;
import nl.knaw.dans.lib.bagpack.mappings.Base;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DansRights;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MetadataLanguages extends Base {

    public static List<Statement> toRDF(Resource resource, Deposit deposit, LanguageResolver languageResolver) {
        return toLanguages(resource, getMetadataLanguages(deposit.getDdm(), languageResolver));
    }

    static List<String> getMetadataLanguages(Document document, LanguageResolver languageResolver) {
        // RIG003
        return XPathEvaluator.strings(document, "//@xml:lang")
            .distinct()
            .map(languageResolver::resolve)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    static List<Statement> toLanguages(Resource resource, Collection<String> metadataLanguages) {
        return toBasicTerms(resource, DansRights.dansMetadataLanguage, metadataLanguages);
    }
}
