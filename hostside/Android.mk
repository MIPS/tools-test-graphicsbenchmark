# Copyright (C) 2018 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Make a mock compatibility suite to test
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_MODULE := GfxBenchmarkTest

LOCAL_MODULE_TAGS := optional

LOCAL_JAVA_LIBRARIES := tradefed graphicsbenchmarkhelper-host

LOCAL_COMPATIBILITY_SUITE := general-tests

include $(BUILD_HOST_JAVA_LIBRARY)

-include tools/tradefederation/core/error_prone_rules.mk

# Build all sub-directories
include $(call all-makefiles-under,$(LOCAL_PATH))