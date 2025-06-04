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
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DansTS;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemporalCoverage extends Base {
    public static List<Statement> toRDF(Resource resource, Deposit deposit) {
        return toTemporalCoverages(resource, getTemporalCoverages(deposit.getDdm()));
    }

    static List<String> getTemporalCoverages(Document ddm) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:temporal")
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
    }

    static List<Statement> toTemporalCoverages(Resource resource, Collection<String> temporalCoverages) {
        return toBasicTerms(resource, DansTS.dansTemporalCoverage, temporalCoverages);
    }

}
