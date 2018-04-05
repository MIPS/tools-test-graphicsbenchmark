/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.tradefed.testtype;

import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.gfx.benchmark.ApkInfo;
import com.android.tradefed.config.Option;
import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.device.ITestDevice;
import com.android.tradefed.result.CollectingTestListener;
import com.android.tradefed.result.ITestInvocationListener;
import com.android.tradefed.testtype.IDeviceTest;
import com.android.tradefed.testtype.IRemoteTest;
import com.android.tradefed.util.RunUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class GraphicsBenchmarkHostsideController implements IRemoteTest, IDeviceTest {
    // Package and class of the device side test.
    private static final String PACKAGE = "com.google.android.gfx.benchmark.test";
    private static final String CLASS = PACKAGE + ".GraphicsBenchmarkTest";

    private static final Map<String, String> testList =
            ImmutableMap.of(
                    "testSniper3d", "Sniper3D_3242.apk",
                    "testAfterpulse", "afterpulse-v1.9.0.apk");

    static final String AJUR_RUNNER = "android.support.test.runner.AndroidJUnitRunner";
    private static final long DEFAULT_TEST_TIMEOUT_MS = 10 * 60 * 1000L; //10min
    private static final long DEFAULT_MAX_TIMEOUT_TO_OUTPUT_MS = 10 * 60 * 1000L; //10min

    private ITestDevice mDevice;
    @Override
    public void setDevice(ITestDevice device) {
        mDevice = device;
    }

    @Override
    public ITestDevice getDevice() {
        return mDevice;
    }

    @Option(name = "apk-dir", description = "Directory contains the APKs for benchmarks")
    private String mApkDir;

    @Override
    public void run(ITestInvocationListener listener) throws DeviceNotAvailableException {
        // TODO: shard this.
        for (ApkInfo apk : ApkInfo.values()) {
            getDevice().installPackage(new File(mApkDir, apk.getFileName()), true);
            runDeviceTests(PACKAGE, CLASS, "run[" + apk.name() + "]");
        }
    }

    // TODO: Migrate to use BaseHostJUnit4Test when available.
    /**
     * Method to run an installed instrumentation package.
     *
     * @param pkgName the name of the package to run.
     * @param testClassName the name of the test class to run.
     * @param testMethodName the name of the method to run.
     */
    private void runDeviceTests(String pkgName, String testClassName, String testMethodName)
            throws DeviceNotAvailableException {
        RemoteAndroidTestRunner testRunner =
                new RemoteAndroidTestRunner(pkgName, AJUR_RUNNER, getDevice().getIDevice());

        testRunner.setMethodName(testClassName, testMethodName);

        testRunner.addInstrumentationArg(
                "timeout_msec", Long.toString(DEFAULT_TEST_TIMEOUT_MS));
        testRunner.setMaxTimeout(DEFAULT_MAX_TIMEOUT_TO_OUTPUT_MS, TimeUnit.MILLISECONDS);

        CollectingTestListener listener = new CollectingTestListener();
        getDevice().runInstrumentationTests(testRunner, listener);
    }
}