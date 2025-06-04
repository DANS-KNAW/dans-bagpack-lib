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
package nl.knaw.dans.lib.bagpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.dans.lib.bagpack.datacite.DataciteConverter;
import nl.knaw.dans.lib.bagpack.datacite.DataciteSerializer;
import nl.knaw.dans.lib.bagpack.deposit.CountryResolver;
import nl.knaw.dans.lib.bagpack.deposit.Deposit;
import nl.knaw.dans.lib.bagpack.deposit.LanguageResolver;
import nl.knaw.dans.lib.bagpack.oaiore.OaiOreConverter;
import nl.knaw.dans.lib.bagpack.oaiore.OaiOreSerializer;
import nl.knaw.dans.lib.bagpack.pidmapping.PidMappingConverter;
import nl.knaw.dans.lib.bagpack.pidmapping.PidMappingSerializer;

public class BagPackWriterFactory {

    private final DataciteSerializer dataciteSerializer;
    private final PidMappingSerializer pidMappingSerializer;
    private final OaiOreSerializer oaiOreSerializer;

    private final DataciteConverter dataciteConverter;
    private final PidMappingConverter pidMappingConverter;
    private final OaiOreConverter oaiOreConverter;

    public BagPackWriterFactory(ObjectMapper objectMapper, LanguageResolver languageResolver, CountryResolver countryResolver) {
        this.dataciteSerializer = new DataciteSerializer();
        this.pidMappingSerializer = new PidMappingSerializer();
        this.oaiOreSerializer = new OaiOreSerializer(objectMapper);
        this.dataciteConverter = new DataciteConverter();
        this.pidMappingConverter = new PidMappingConverter();
        this.oaiOreConverter = new OaiOreConverter(languageResolver, countryResolver);
    }

    public BagPackWriter createBagPackWriter(Deposit deposit) {
        return new BagPackWriter(
            deposit,
            dataciteSerializer,
            pidMappingSerializer,
            oaiOreSerializer,
            dataciteConverter,
            pidMappingConverter,
            oaiOreConverter
        );
    }
}
