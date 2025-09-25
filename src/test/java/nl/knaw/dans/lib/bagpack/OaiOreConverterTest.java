package nl.knaw.dans.lib.bagpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.dans.lib.bagpack.oaiore.OaiOreConverter;
import nl.knaw.dans.lib.bagpack.oaiore.OaiOreSerializer;
import nl.knaw.dans.lib.bagpack.testutils.TestCountryResolverSingleton;
import nl.knaw.dans.lib.bagpack.testutils.TestDepositManager;
import nl.knaw.dans.lib.bagpack.testutils.TestLanguageResolverSingleton;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.apache.jena.sparql.util.Context;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.nio.file.Path;

public class OaiOreConverterTest {

    @Test
    public void test() throws Exception {
        var depositManager = new TestDepositManager();
        var converter = new OaiOreConverter(TestLanguageResolverSingleton.getInstance(), TestCountryResolverSingleton.getInstance());
        var deposit = depositManager.loadDeposit(Path.of("/input/c169676f-5315-4d86-bde0-a62dbc915228"), "TEST-DATASUPPLIER");
        deposit.setNbn("urn:nbn:nl:ui:13-4c-1a2b");
        var model = converter.convert(deposit);

        // Serialize model to XML
        var oreSerializer = new OaiOreSerializer(new ObjectMapper());
        var s = oreSerializer.serializeAsJsonLd(model);
        System.out.println(s);



    }
    
    @Test
    public void graphLessJsonLD() throws Exception {
        var model = ModelFactory.createDefaultModel();
        var ns = "http://example.org/";
        model.setNsPrefix("ex", ns);

        var person = model.createResource(ns + "jane")
            .addProperty(model.createProperty(ns + "name"), "Jane Smith")
            .addProperty(model.createProperty(ns + "age"), "25");

        var context = new Context();
        var frame = "{\n" +
            "  \"@context\": {\n" +
            "    \"ex\": \"http://example.org/\",\n" +
            "    \"name\": \"ex:name\",\n" +
            "    \"age\": \"ex:age\"\n" +
            "  }\n" +
            "}";

        context.set(JsonLD10Writer.JSONLD_FRAME, frame);

        var writer = RDFWriter.create()
            .format(RDFFormat.JSONLD10_FRAME_PRETTY)
            .source(DatasetFactory.wrap(model).getDefaultModel())
            .context(context)
            .build();

        var outputWriter = new StringWriter();
        writer.output(outputWriter);
        System.out.println(outputWriter);
    }
    
    

}
