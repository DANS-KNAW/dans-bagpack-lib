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
package nl.knaw.dans.lib.bagpack.oaiore;

import nl.knaw.dans.lib.bagpack.deposit.CountryResolver;
import nl.knaw.dans.lib.bagpack.deposit.Deposit;
import nl.knaw.dans.lib.bagpack.deposit.LanguageResolver;
import nl.knaw.dans.lib.bagpack.deposit.PayloadFile;
import nl.knaw.dans.lib.bagpack.mappings.AlternativeTitles;
import nl.knaw.dans.lib.bagpack.mappings.Audiences;
import nl.knaw.dans.lib.bagpack.mappings.Authors;
import nl.knaw.dans.lib.bagpack.mappings.CollectionDates;
import nl.knaw.dans.lib.bagpack.mappings.Contributors;
import nl.knaw.dans.lib.bagpack.mappings.DansRelations;
import nl.knaw.dans.lib.bagpack.mappings.DataFile;
import nl.knaw.dans.lib.bagpack.mappings.Descriptions;
import nl.knaw.dans.lib.bagpack.mappings.DistributionDate;
import nl.knaw.dans.lib.bagpack.mappings.Distributors;
import nl.knaw.dans.lib.bagpack.mappings.GrantNumbers;
import nl.knaw.dans.lib.bagpack.mappings.InCollection;
import nl.knaw.dans.lib.bagpack.mappings.Keywords;
import nl.knaw.dans.lib.bagpack.mappings.Languages;
import nl.knaw.dans.lib.bagpack.mappings.License;
import nl.knaw.dans.lib.bagpack.mappings.MetadataLanguages;
import nl.knaw.dans.lib.bagpack.mappings.OtherIds;
import nl.knaw.dans.lib.bagpack.mappings.PersonalData;
import nl.knaw.dans.lib.bagpack.mappings.ProductionDate;
import nl.knaw.dans.lib.bagpack.mappings.Publications;
import nl.knaw.dans.lib.bagpack.mappings.RightsHolders;
import nl.knaw.dans.lib.bagpack.mappings.Sources;
import nl.knaw.dans.lib.bagpack.mappings.SpatialCoverage;
import nl.knaw.dans.lib.bagpack.mappings.Subjects;
import nl.knaw.dans.lib.bagpack.mappings.TemporalCoverage;
import nl.knaw.dans.lib.bagpack.mappings.Terms;
import nl.knaw.dans.lib.bagpack.mappings.Titles;
import nl.knaw.dans.lib.bagpack.mappings.VaultMetadata;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.ORE;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SchemaDO;

import java.time.OffsetDateTime;

public class OaiOreConverter {

    private final LanguageResolver languageResolver;
    private final CountryResolver countryResolver;

    public OaiOreConverter(LanguageResolver languageResolver, CountryResolver countryResolver) {
        this.languageResolver = languageResolver;
        this.countryResolver = countryResolver;
    }

    public Model convert(Deposit deposit) {
        var model = ModelFactory.createDefaultModel();

        var resourceMap = createResourceMap(deposit, model);
        var resource = createAggregation(deposit, model);

        model.add(Titles.toRDF(resource, deposit));
        AlternativeTitles.toRDF(resource, deposit)
            .ifPresent(model::add);

        model.add(OtherIds.toRDF(resource, deposit));
        model.add(Authors.toRDF(resource, deposit));

        model.add(Descriptions.toRDF(resource, deposit));
        model.add(Subjects.toRDF(resource, deposit));
        model.add(Keywords.toRDF(resource, deposit));
        model.add(Publications.toRDF(resource, deposit));
        model.add(Languages.toRDF(resource, deposit, languageResolver));

        ProductionDate.toRDF(resource, deposit)
            .ifPresent(model::add);

        model.add(Contributors.toRDF(resource, deposit));
        model.add(GrantNumbers.toRDF(resource, deposit));
        model.add(Distributors.toRDF(resource, deposit));

        DistributionDate.toRDF(resource, deposit)
            .ifPresent(model::add);

        model.add(CollectionDates.toRDF(resource, deposit));
        model.add(Sources.toRDF(resource, deposit));

        model.add(RightsHolders.toRDF(resource, deposit));
        model.add(PersonalData.toRDF(resource, deposit));
        model.add(Languages.toRDF(resource, deposit, languageResolver));

        model.add(Audiences.toRDF(resource, deposit));
        model.add(InCollection.toRDF(resource, deposit));
        model.add(DansRelations.toRDF(resource, deposit));

        model.add(TemporalCoverage.toRDF(resource, deposit));
        model.add(SpatialCoverage.toRDF(resource, deposit, countryResolver));

        model.add(VaultMetadata.toRDF(resource, deposit));
        model.add(MetadataLanguages.toRDF(resource, deposit, languageResolver));
        License.toRDF(resource, deposit).ifPresent(model::add);

        model.add(Terms.toRDF(resource, deposit));

        model.add(model.createStatement(
            resourceMap,
            ORE.describes,
            resource
        ));

        return model;
    }

    Resource createResourceMap(Deposit deposit, Model model) {
        var resourceMap = model.createResource("urn:uuid:" + deposit.getId());
        var resourceMapType = model.createStatement(resourceMap, RDF.type, ORE.ResourceMap);

        model.add(resourceMapType);
        model.add(model.createStatement(
            resourceMap,
            DCTerms.modified,
            OffsetDateTime.now().toString()
        ));

        var creator = model.createResource();
        model.add(model.createStatement(
            creator,
            FOAF.name,
            "DANS Vault Service"
        ));

        model.add(model.createStatement(
            resourceMap,
            DCTerms.creator,
            creator
        ));

        return resourceMap;
    }

    Resource createAggregatedResource(Model model, PayloadFile payloadFile) {
        var resource = model.createResource("urn:uuid:" + payloadFile.getId());

        model.add(model.createStatement(resource, RDF.type, ORE.AggregatedResource));
        model.add(model.createStatement(resource, SchemaDO.name, payloadFile.getPath().toString()));
        model.add(DataFile.toRDF(resource, payloadFile));
        return resource;
    }

//    Resource createAggregatedResource(Model model, PayloadFile payloadFile) {
//        var resource = model.createResource("urn:uuid:" + payloadFile.getId())
//            .addProperty(RDF.type, ORE.AggregatedResource)
//            .addProperty(SchemaDO.name, payloadFile.getPath().toString());
//        model.add(DataFile.toRDF(resource, payloadFile));
//        return resource;
//    }

    Resource createAggregation(Deposit deposit, Model model) {
        var resource = model.createResource(deposit.getNbn());
        var type = model.createStatement(resource, RDF.type, ORE.Aggregation);

        model.add(type);

        if (deposit.getPayloadFiles() != null) {
            for (var file : deposit.getPayloadFiles()) {
                var fileResource = createAggregatedResource(model, file);

                model.add(model.createStatement(
                    resource,
                    ORE.aggregates,
                    fileResource
                ));
            }
        }

        return resource;
    }


//    Resource createAggregation(Deposit deposit, Model model) {
//        var resource = model.createResource(deposit.getNbn())
//            .addProperty(RDF.type, ORE.Aggregation);
//
//        if (deposit.getPayloadFiles() != null) {
//            for (var file : deposit.getPayloadFiles()) {
//                var fileResource = createAggregatedResource(model, file);
//                resource.addProperty(ORE.aggregates, fileResource);
//            }
//        }
//
//        return resource;
//    }
}
