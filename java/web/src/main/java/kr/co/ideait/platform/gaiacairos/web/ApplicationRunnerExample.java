package kr.co.ideait.platform.gaiacairos.web;

import kr.co.ideait.iframework.event.listener.AfterTransactionEvent;
import kr.co.ideait.iframework.event.listener.BeforeTransactionEvent;
import kr.co.ideait.iframework.event.listener.PlatformEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationRunnerExample implements ApplicationRunner {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void run(ApplicationArguments args) throws Exception{

        log.info("Arrays.toString(args) = " + Arrays.toString(args.getSourceArgs()));
        log.info("시스템 Start 완료!!");

        applicationEventPublisher.publishEvent(PlatformEventDto.builder().userId("1"));


        // beforeTransactionEvent 생성
        BeforeTransactionEvent beforeTransactionEvent = () -> log.info("commit 시작, {}", LocalTime.now());

        // afterTransactionEvent 생성
        AfterTransactionEvent afterTransactionEvent = new AfterTransactionEvent() {
            @Override
            public void completed() {
                log.info("트랜잭션 끝, {}", LocalTime.now());
            }

            @Override
            public void callback() {
                log.info("commit 종료, {}", LocalTime.now());
            }
        };

        // 이벤트 발행
        applicationEventPublisher.publishEvent(beforeTransactionEvent);
        applicationEventPublisher.publishEvent(afterTransactionEvent);
    }
}

