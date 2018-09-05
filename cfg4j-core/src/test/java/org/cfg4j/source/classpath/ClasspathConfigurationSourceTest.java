/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
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
package org.cfg4j.source.classpath;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;


@ExtendWith(MockitoExtension.class)
class ClasspathConfigurationSourceTest {




  private TempConfigurationClasspathRepo classpathRepo;
  private ConfigFilesProvider configFilesProvider;
  private ClasspathConfigurationSource source;

  @BeforeEach
  public void setUp() throws Exception {
    classpathRepo = new TempConfigurationClasspathRepo();

    source = new ClasspathConfigurationSource();
    source.init();
  }

  @AfterEach
  public void tearDown() throws Exception {
    classpathRepo.close();
  }

  @Test
  public void getConfigurationReadsFromGivenPath() throws Exception {
    Environment environment = new ImmutableEnvironment("otherApplicationConfigs");

    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  public void getConfigurationDisallowsLeadingSlashInClasspathLocation() throws Exception {
    Environment environment = new ImmutableEnvironment("/otherApplicationConfigs");

    // FIXME: expectedException.expect(MissingEnvironmentException.class);
    source.getConfiguration(environment);
  }

  @Test
  public void getConfigurationReadsFromGivenFiles() throws Exception {
    configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Arrays.asList(
            Paths.get("application.properties"),
            Paths.get("otherConfig.properties")
        );
      }
    };

    source = new ClasspathConfigurationSource(configFilesProvider);
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsOnlyKeys("some.setting", "otherConfig.setting");
  }

  @Test
  public void getConfigurationThrowsOnMissingEnvironment() throws Exception {
    // FIXME: expectedException.expect(MissingEnvironmentException.class);
    source.getConfiguration(new ImmutableEnvironment("awlerijawoetinawwerlkjn"));
  }

  @Test
  public void getConfigurationThrowsOnMissingConfigFile() throws Exception {
    configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Collections.singletonList(
            Paths.get("nonexistent.properties")
        );
      }
    };

    source = new ClasspathConfigurationSource(configFilesProvider);

    // FIXME: expectedException.expect(IllegalStateException.class);
    source.getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void getConfigurationThrowsOnMalformedConfigFile() throws Exception {
    configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Collections.singletonList(
            Paths.get("malformed.properties")
        );
      }
    };

    source = new ClasspathConfigurationSource(configFilesProvider);

    // FIXME: expectedException.expect(IllegalStateException.class);
    source.getConfiguration(new DefaultEnvironment());
  }

}