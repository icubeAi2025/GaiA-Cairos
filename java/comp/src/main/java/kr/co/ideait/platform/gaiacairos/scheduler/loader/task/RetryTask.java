package kr.co.ideait.platform.gaiacairos.scheduler.loader.task;

import com.google.common.collect.Maps;
import com.tware.components.scheduler.job.CronJob;
import kr.co.ideait.iframework.springconfig.ApplicationContextProvider;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.config.CoreConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.util.BizServiceInvoker;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import org.jasypt.encryption.StringEncryptor;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
public class RetryTask extends CronJob {

    @Override
    protected void doExecute(JobExecutionContext context, JobKey jobKey) {
        SystemLogComponent systemLogComponent = (SystemLogComponent) ApplicationContextProvider.getApplicationContext().getBean("systemLogComponent");
        BizServiceInvoker bizServiceInvoker = (BizServiceInvoker) ApplicationContextProvider.getApplicationContext().getBean("bizServiceInvoker");

        int page = 1;
        int size = 1;

        ApiLogMybatisParam.ApiLogListInput apiLogDto = new ApiLogMybatisParam.ApiLogListInput();
        apiLogDto.setPageable(PageRequest.of(page - 1, size));
        apiLogDto.setResultCode(HttpStatus.INTERNAL_SERVER_ERROR_500);

        Page<ApiLogMybatisParam.ApiLogOutput> list = systemLogComponent.getApiLogList(apiLogDto);

        log.info("list: {}", list);

        for (ApiLogMybatisParam.ApiLogOutput apiLogOutput : list.getContent()) {
            bizServiceInvoker.invoke("TRAN_PDF_MERGE_SAMPLE", Maps.newHashMap());
        }

    }
}
