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
import nl.knaw.dans.lib.bagpack.mappings.metadata.CollectionDate;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DVCitation;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CollectionDates extends Base {
    private static final Pattern DATES_OF_COLLECTION_PATTERN = Pattern.compile("^(.*)/(.*)$");

    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        var dates = getCollectionDates(deposit.getDdm());
        return toDatesOfCollection(resource, dates);
    }

    static List<CollectionDate> getCollectionDates(Document document) {
        return XPathEvaluator.strings(document, "/ddm:DDM/ddm:dcmiMetadata/ddm:datesOfCollection")
            .map(value -> {
                var matches = DATES_OF_COLLECTION_PATTERN.matcher(value.trim());

                if (matches.matches()) {
                    return CollectionDate.builder()
                        .start(matches.group(1))
                        .end(matches.group(2))
                        .build();
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    }

    static List<Statement> toDatesOfCollection(Resource resource, Collection<CollectionDate> dates) {
        return toComplexTerms(resource, DVCitation.dateOfCollection, dates, (element, date) -> {
            if (date.getStart() != null) {
                element.addProperty(DVCitation.dateOfCollectionStart, date.getStart());
            }
            if (date.getEnd() != null) {
                element.addProperty(DVCitation.dateOfCollectionEnd, date.getEnd());
            }
        });
    }
}
