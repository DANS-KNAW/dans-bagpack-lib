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

import nl.knaw.dans.lib.bagpack.deposit.CountryResolver;
import nl.knaw.dans.lib.bagpack.deposit.Deposit;
import nl.knaw.dans.lib.bagpack.mappings.Base;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.DansTS;
import nl.knaw.dans.lib.bagpack.xml.XPathEvaluator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpatialCoverage extends Base {
    public static List<Statement> toRDF(Resource resource, Deposit deposit, CountryResolver countryResolver) {
        var result = new ArrayList<Statement>();
        result.addAll(toSpatialCoverageControlled(resource, getSpatialCoveragesControlled(deposit.getDdm(), countryResolver)));
        result.addAll(toSpatialCoverageText(resource, getSpatialCoveragesText(deposit.getDdm(), countryResolver)));

        return result;

    }

    static List<String> getSpatialCoveragesControlled(Document ddm, CountryResolver countryResolver) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:spatial")
            .map(String::trim)
            .distinct()
            .filter(countryResolver::isControlledValue)
            .collect(Collectors.toList());
    }

    static List<String> getSpatialCoveragesText(Document ddm, CountryResolver countryResolver) {
        return XPathEvaluator.strings(ddm, "/ddm:DDM/ddm:dcmiMetadata/dcterms:spatial")
            .map(String::trim)
            .distinct()
            .filter(c -> !countryResolver.isControlledValue(c))
            .collect(Collectors.toList());
    }

    static List<Statement> toSpatialCoverageControlled(Resource resource, Collection<String> spatialCoveragesControlled) {
        return toBasicTerms(resource, DansTS.dansSpatialCoverageControlled, spatialCoveragesControlled);
    }

    static List<Statement> toSpatialCoverageText(Resource resource, Collection<String> spatialCoveragesText) {
        return toBasicTerms(resource, DansTS.dansSpatialCoverageText, spatialCoveragesText);
    }
}
