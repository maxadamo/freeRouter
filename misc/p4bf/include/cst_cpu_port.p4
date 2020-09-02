/*
 * Copyright 2019-present GÉANT RARE project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed On an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef _CPU_PORT_P4_
#define _CPU_PORT_P4_

#ifdef _WEDGE100BF32X_
#define CPU_PORT 192
#define RECIR_PORT 68
#else
#define CPU_PORT 64
#define RECIR_PORT 68
#endif

#endif // _CPU_PORT_P4_
