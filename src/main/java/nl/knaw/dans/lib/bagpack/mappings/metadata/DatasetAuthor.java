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
package nl.knaw.dans.lib.bagpack.mappings.metadata;

import lombok.Builder;
import lombok.Value;
import nl.knaw.dans.lib.bagpack.mappings.ids.DAI;
import nl.knaw.dans.lib.bagpack.mappings.ids.ISNI;
import nl.knaw.dans.lib.bagpack.mappings.ids.Identifier;
import nl.knaw.dans.lib.bagpack.mappings.ids.ORCID;
import nl.knaw.dans.lib.bagpack.mappings.metadata.DatasetRelation;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class DatasetAuthor implements DatasetRelation {
    String name;
    String titles;
    String initials;
    String insertions;
    String surname;
    DAI dai;
    ISNI isni;
    ORCID orcid;
    String role;
    String affiliation;

    public String getDisplayName() {
        // initials + insertions + surname
        return Stream.of(
                this.getInitials(),
                this.getInsertions(),
                this.getSurname()
            ).filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    }

    public String getContributorName() {
        // titles + initials + insertions + surname
        var name = Stream.of(
                this.getInitials(),
                this.getInsertions(),
                this.getSurname()
            ).filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));

        if (StringUtils.isNotBlank(this.getAffiliation())) {
            name += " (" + this.getAffiliation() + ")";
        }

        return name;
    }

    public Identifier getIdentifier() {
        var schemes = new Identifier[] {
            this.orcid,
            this.isni,
            this.dai,
        };

        // return first match
        for (var scheme : schemes) {
            if (scheme != null) {
                return scheme;
            }
        }

        return null;
    }

}
