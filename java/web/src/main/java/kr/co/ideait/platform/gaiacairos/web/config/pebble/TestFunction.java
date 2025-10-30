package kr.co.ideait.platform.gaiacairos.web.config.pebble;

import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("btnId");
        names.add("cls");
        return names;
        //return Collections.emptyList();
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self,
                          EvaluationContext context, int lineNumber) {
        return "Tested!";
    }
}