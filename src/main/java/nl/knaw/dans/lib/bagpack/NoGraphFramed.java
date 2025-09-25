package nl.knaw.dans.lib.bagpack;

// File: NoGraphFramed410.java

import com.github.jsonldjava.core.JsonLdOptions;
import nl.knaw.dans.lib.bagpack.mappings.vocabulary.ORE;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SchemaDO;

import java.util.UUID;

public class NoGraphFramed {

    private static final String SCHEMA = "https://schema.org/";

    public static void main(String[] args) {
        // --- DATA ---
        Model m = ModelFactory.createDefaultModel();
//        m.setNsPrefix("schema", SCHEMA);
//        m.setNsPrefix("ore", ORE.NS);
//        m.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
//        m.setNsPrefix("dvcore", "https://dataverse.org/schema/core#");
//        m.setNsPrefix("dansDataVaultMetadata", "https://dev.archaeology.datastations.nl/schema/dansDataVaultMetadata#");
//        m.setNsPrefix("dansRelationMetadata", "https://dev.archaeology.datastations.nl/schema/dansRelationMetadata#");
//        m.setNsPrefix("dansRights", "https://dev.archaeology.datastations.nl/schema/dansRights#");
//        m.setNsPrefix("vocab", "http://example.org/vocab/");
//
//        //        Resource person = m.createResource("http://example.org/person/1")
//        //            .addProperty(RDF.type, m.createResource(SCHEMA + "Person"))
//        //            .addProperty(m.createProperty(SCHEMA, "name"), "Jan van Mansum")
//        //            .addProperty(m.createProperty(SCHEMA, "email"), "jan@example.org");
//        //
//        //        Resource org = m.createResource("http://example.org/organization/1")
//        //            .addProperty(RDF.type, m.createResource(SCHEMA + "Organization"));
//        //        person.addProperty(m.createProperty(SCHEMA, "affiliation"), org);

        var resourceMap = m.createResource("urn:uuid:" + UUID.randomUUID().toString())
            .addProperty(RDF.type, ORE.ResourceMap);

        var resource = m.createResource("urn:nbn:ui:13-" + UUID.randomUUID().toString())
            .addProperty(RDF.type, ORE.Aggregation)
            .addProperty(RDF.type, SchemaDO.Dataset);

        resourceMap.addProperty(ORE.describes, resource);

        String frame = """
            {
            "@context": { "schema": "https://schema.org/",
                "author": "http://purl.org/dc/terms/creator",
                "citation": "https://dataverse.org/schema/citation/",
                "dansDataVaultMetadata": "https://dev.archaeology.datastations.nl/schema/dansDataVaultMetadata#",
                "dansRelationMetadata": "https://dev.archaeology.datastations.nl/schema/dansRelationMetadata#",
                "dansRights": "https://dev.archaeology.datastations.nl/schema/dansRights#",
                "dateOfDeposit": "http://purl.org/dc/terms/dateSubmitted",
                "dcterms": "http://purl.org/dc/terms/",
                "dvcore": "https://dataverse.org/schema/core#",
                "ore": "http://www.openarchives.org/ore/terms/",
                "scheme": "http://www.w3.org/2004/02/skos/core#inScheme",
                "subject": "http://purl.org/dc/terms/subject",
                "termName": "https://schema.org/name",
                "title": "http://purl.org/dc/terms/title",
                "vocabularyName": "https://dataverse.org/schema/vocabularyName",
                "vocabularyUri": "https://dataverse.org/schema/vocabularyUri"
            },
            "@type": "ore:ResourceMap"
            }
            """;

        JsonLdOptions opts = new JsonLdOptions();
        opts.setOmitGraph(true);

        var ctx = new JsonLDWriteContext();
        ctx.setFrame(frame);
        ctx.setOptions(opts);

        String out = RDFWriter.create()
            .source(m)
            .format(RDFFormat.JSONLD10_FRAME_PRETTY)
            .context(ctx)
            .asString();

        System.out.println(out);
    }
}
