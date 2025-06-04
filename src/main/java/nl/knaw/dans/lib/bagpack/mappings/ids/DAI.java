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
package nl.knaw.dans.lib.bagpack.mappings.ids;

public class DAI extends Identifier {

    public DAI(String value) {
        super(value);
    }

    @Override
    public String getScheme() {
        return "DAI";
    }

    @Override
    public String getSchemeURI() {
        // TODO verify
        // there seems to be no website, but wikipedia says it is ISNI.
        // https://en.wikipedia.org/wiki/Digital_Author_Identifier
        return "https://dai.nl/";
    }

}
