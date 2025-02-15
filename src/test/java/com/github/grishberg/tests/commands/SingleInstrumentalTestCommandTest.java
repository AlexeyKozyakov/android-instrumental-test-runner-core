package com.github.grishberg.tests.commands;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.ddmlib.testrunner.TestRunResult;
import com.android.utils.ILogger;
import com.github.grishberg.tests.*;
import com.github.grishberg.tests.commands.reports.TestXmlReportsGenerator;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by grishberg on 22.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleInstrumentalTestCommandTest {
    private static final String PROJECT_NAME = "test_project";
    private static final String TEST_CLASS = "com.test.TestClass";
    private static final String TEST_NAME = "test1";
    private static final String TEST_NAME_WITH_DEVICE = "test1[phone-1]";
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    IDevice device;
    @Mock
    Environment environment;
    @Mock
    RunnerLogger logger;
    @Mock
    TestRunnerContext context;
    @Mock
    TestRunnerBuilder testRunnerBuilder;
    @Mock
    TestXmlReportsGenerator reportsGenerator;
    @Mock
    RemoteAndroidTestRunner testRunner;
    @Mock
    TestRunResult testRunResult;
    @Mock
    ILogger iLogger;
    @Mock
    File coverageDir;

    @Mock
    ProcessCrashHandler processCrashedHandler;
    private SingleInstrumentalTestCommand testCommand;
    private HashMap<String, String> args = new HashMap<>();
    private InstrumentalExtension ext = new InstrumentalExtension();
    private ArrayList<TestPlanElement> testElements = new ArrayList<>();
    private Map<String, String> instrumentationArgs;
    private TestIdentifier currentTest = new TestIdentifier(TEST_CLASS, TEST_NAME);

    @Before
    public void setUp() {
        when(processCrashedHandler.provideFailMessageOnProcessCrashed(any(), any()))
                .thenReturn("Process was crashed. See logcat to details.");
        when(context.getInstrumentalInfo()).thenReturn(ext);
        when(context.getEnvironment()).thenReturn(environment);
        when(context.getProcessCrashedHandler()).thenReturn(processCrashedHandler);
        when(context.getLogger()).thenReturn(mock(RunnerLogger.class));
        when(environment.getCoverageDir()).thenReturn(coverageDir);
        doAnswer((Answer<TestRunnerBuilder>) invocation -> {
            instrumentationArgs = invocation.getArgument(2);
            return testRunnerBuilder;
        }).when(context).createTestRunnerBuilder(any(), any(), any(), any(), any());

        when(testRunnerBuilder.getTestRunListener()).thenReturn(reportsGenerator);
        when(testRunnerBuilder.getTestRunner()).thenReturn(testRunner);
        when(testRunnerBuilder.getRunTestLogger()).thenReturn(iLogger);
        when(testRunnerBuilder.getCoverageFile()).thenReturn("coverage_file");

        when(reportsGenerator.getRunResult()).thenReturn(testRunResult);
        when(reportsGenerator.getCurrentTest()).thenReturn(currentTest);

        when(deviceWrapper.getName()).thenReturn("test_device");
        testElements.add(new TestPlanElement("", TEST_NAME, TEST_CLASS));
        testCommand = new SingleInstrumentalTestCommand(PROJECT_NAME, "test_prefix", args, testElements);
    }

    @Test
    public void initWithClass() throws Exception {
        testCommand.execute(deviceWrapper, context);

        Assert.assertEquals("com.test.TestClass#test1", instrumentationArgs.get("class"));
    }

    @Test
    public void testWhenCoverageEnabled() throws Exception {
        ext.setCoverageEnabled(true);

        testCommand.execute(deviceWrapper, context);

        verify(deviceWrapper).pullCoverageFile(ext,
                "test_device#test_prefix",
                "coverage_file",
                coverageDir,
                iLogger);
    }

    @Test
    public void pullCoverageFileWhenEnabledCoverage() throws Exception {
        ext.setCoverageEnabled(true);
        when(environment.getCoverageDir()).thenReturn(new File("/coverage"));

        testCommand.execute(deviceWrapper, context);

        verify(deviceWrapper).pullCoverageFile(
                any(InstrumentalExtension.class),
                anyString(),
                anyString(),
                any(File.class),
                any(ILogger.class));
    }

    @Test(expected = CommandExecutionException.class)
    public void throwExecuteCommandExceptionWhenSomeDeviceException() throws Exception {
        Mockito.doThrow(new IOException(new Throwable())).when(testRunner)
                .run((ITestRunListener[]) any());

        testCommand.execute(deviceWrapper, context);
    }

    @Test
    public void failTestWhenProcessCrashed() throws Exception {
        doAnswer(invocation -> {
            for (Object listener : invocation.getArguments()) {
                ((ITestRunListener) listener).testStarted(
                        new TestIdentifier(TEST_CLASS, TEST_NAME_WITH_DEVICE));
            }
            throw new ProcessCrashedException("Process crashed");
        }).when(testRunner).run((ITestRunListener[]) any());

        testCommand.execute(deviceWrapper, context);

        verify(reportsGenerator).failLastTest("Process was crashed. See logcat to details.");
    }

    @Test
    public void callTestRunEndedFromReporterWhenProcessCrashed() throws Exception {
        doAnswer(invocation -> {
            for (Object listener : invocation.getArguments()) {
                ((ITestRunListener) listener).testStarted(
                        new TestIdentifier(TEST_CLASS, TEST_NAME_WITH_DEVICE));
            }
            throw new ProcessCrashedException("Process crashed");
        }).when(testRunner).run((ITestRunListener[]) any());

        testCommand.execute(deviceWrapper, context);

        verify(reportsGenerator).testRunEnded(anyLong(), any());
    }

    @Test
    public void handleProcessCrashedWhenProcessCrashed() throws Exception {
        doAnswer(invocation -> {
            for (Object listener : invocation.getArguments()) {
                ((ITestRunListener) listener).testStarted(
                        new TestIdentifier(TEST_CLASS, TEST_NAME_WITH_DEVICE));
            }
            throw new ProcessCrashedException("Process crashed");
        }).when(testRunner).run((ITestRunListener[]) any());

        testCommand.execute(deviceWrapper, context);
        verify(processCrashedHandler).provideFailMessageOnProcessCrashed(deviceWrapper, currentTest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenGivenEmptyTestList() throws Exception {
        testCommand = new SingleInstrumentalTestCommand(PROJECT_NAME, "test_prefix", args, new ArrayList<>());

        testCommand.execute(deviceWrapper, context);
    }
}