package io.siddhi.extension.io.file;

import io.siddhi.extension.io.file.util.TestUtils;
import io.siddhi.extension.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.TestException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.metrics.core.Level;
import org.wso2.carbon.metrics.core.MetricManagementService;
import org.wso2.carbon.metrics.core.MetricService;
import org.wso2.carbon.metrics.core.Metrics;
import org.wso2.carbon.si.metrics.core.internal.MetricsDataHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSourceMetricsTestCase {

    private static final Logger log = Logger.getLogger(FileSourceMetricsTestCase.class);
    private AtomicInteger count = new AtomicInteger();
    private int waitTime = 10000;
    private int timeout = 30000;
    private String dirUri, moveAfterProcessDir;
    private File sourceRoot, newRoot, movedFiles;
    private Metrics metrics;
    private MetricService metricService;
    private final String siddhiAppName = "TestSiddhiApp";

    @BeforeClass
    public void init() {
        ClassLoader classLoader = FileSourceMetricsTestCase.class.getClassLoader();
        String rootPath = classLoader.getResource("files").getFile();
        sourceRoot = new File(rootPath + "/repo");
        dirUri = rootPath + "/new";
        newRoot = new File(dirUri);
        moveAfterProcessDir = rootPath + "/moved_files";

    }

    @BeforeMethod
    public void doBeforeMethod() throws ConfigurationException {
        metrics = new Metrics(TestUtils.getConfigProvider("conf/metrics-prometheus.yaml"));
        metrics.activate();
        metricService = metrics.getMetricService();
        MetricManagementService metricManagementService = metrics.getMetricManagementService();
        metricManagementService.setRootLevel(Level.ALL);
        metricManagementService.stopReporters();
        MetricsDataHolder.getInstance().setMetricService(metricService);
        MetricsDataHolder.getInstance().setMetricManagementService(metricManagementService);
        count.set(0);
        try {
            FileUtils.copyDirectory(sourceRoot, newRoot);
            movedFiles = new File(moveAfterProcessDir);
        } catch (IOException e) {
            throw new TestException("Failed to copy files from " +
                    sourceRoot.getAbsolutePath() +
                    " to " +
                    newRoot.getAbsolutePath() +
                    " which are required for tests. Hence aborting tests.", e);
        }

    }

    @AfterMethod
    public void doAfterMethod() {
        log.info("Deactivating Metrics");
        metrics.deactivate();
        try {
            FileUtils.deleteDirectory(newRoot);
            FileUtils.deleteDirectory(movedFiles);
        } catch (IOException e) {
            throw new TestException("Failed to delete files in due to " + e.getMessage(), e);
        }
    }

    @Test
    public void testLineCount() throws IOException {
        List<String> files = new ArrayList<>();
//        File file  = new File(dirUri + "/line/json/logs.txt");
        /*for (File f : file.listFiles()) {
            files.add(f.getPath());
            System.out.println(Utils.getFileSize(f.getPath()));
            System.out.println(Utils.getLinesCount(f.getPath()));
        }*/
        String uri = "file:" + dirUri + "/line/json/logs%20(4th%20copy).txt";
        System.out.println(Utils.getFileSize(uri));
        System.out.println(Utils.getLinesCount(uri));

    }




}
