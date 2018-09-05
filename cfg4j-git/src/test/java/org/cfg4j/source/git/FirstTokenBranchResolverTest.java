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
package org.cfg4j.source.git;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.cfg4j.source.context.environment.Environment;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


class FirstTokenBranchResolverTest {




  @Mock
  private Environment environment;

  private FirstTokenBranchResolver branchResolver;

  @BeforeEach
  public void setUp() throws Exception {
    branchResolver = new FirstTokenBranchResolver();
  }

  @Test
  public void resolvesEmptyStringToMaster() throws Exception {
    when(environment.getName()).thenReturn("");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("master");
  }

  @Test
  public void resolvesWhitespacesToMaster() throws Exception {
    when(environment.getName()).thenReturn("   ");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("master");
  }

  @Test
  public void supportsSingleToken() throws Exception {
    when(environment.getName()).thenReturn("us-west-1");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("us-west-1");
  }

  @Test
  public void usesFirstTokenAsBranchName() throws Exception {
    when(environment.getName()).thenReturn("us-west-1/local/path");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("us-west-1");
  }
}