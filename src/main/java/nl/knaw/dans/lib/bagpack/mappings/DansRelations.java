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
import nl.knaw.dans.lib.bagpack.mappings.metadata.DansRelation;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DansRel;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DansRelations extends Base {
    private static final Map<String, String> labelToType = new HashMap<>();

    static {
        labelToType.put("relation", "relation");
        labelToType.put("conformsTo", "conforms_to");
        labelToType.put("hasFormat", "has_format");
        labelToType.put("hasPart", "has_part");
        labelToType.put("references", "references");
        labelToType.put("replaces", "replaces");
        labelToType.put("requires", "requires");
        labelToType.put("hasVersion", "has_version");
        labelToType.put("isFormatOf", "is_format_of");
        labelToType.put("isPartOf", "is_part_of");
        labelToType.put("isReferencedBy", "is_referenced_by");
        labelToType.put("isReplacedBy", "is_replaced_by");
        labelToType.put("isRequiredBy", "is_required_by");
        labelToType.put("isVersionOf", "is_version_of");
    }

    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        var relations = getDansRelations(deposit.getDdm());
        return toDansRelations(resource, relations);
    }

    static List<DansRelation> getDansRelations(Document document) {
        var queries = labelToType.keySet().stream()
            .map(name -> "/ddm:DDM/ddm:dcmiMetadata/ddm:" + name)
            .toArray(String[]::new);

        return XPathEvaluator.nodes(document.getDocumentElement(), queries)
            .map(item -> {
                var label = labelToType.get(item.getLocalName());
                var text = item.getTextContent();
                var uri = XPathEvaluator.strings(item, "@href").findFirst().orElse(null);

                return DansRelation.builder()
                    .type(label)
                    .text(text)
                    .uri(uri)
                    .build();
            })
            .collect(Collectors.toList());
    }

    static List<Statement> toDansRelations(Resource resource, Collection<DansRelation> relations) {
        return toComplexTerms(resource, DansRel.dansRelation, relations, (element, relation) -> {
            if (relation.getText() != null) {
                element.addProperty(DansRel.dansRelationText, relation.getText());
            }

            if (relation.getType() != null) {
                element.addProperty(DansRel.dansRelationType, relation.getType());
            }

            if (relation.getUri() != null) {
                element.addProperty(DansRel.dansRelationURI, relation.getUri());
            }
        });
    }
}
