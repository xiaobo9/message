package xiaobo9.message.sonar.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class SonarWebHookMessage {

    /**
     * serverUrl : http://localhost:9000
     * taskId : AVh21JS2JepAEhwQ-b3u
     * status : SUCCESS
     * analysedAt : 2016-11-18T10:46:28+0100
     * revision : c739069ec7105e01303e8b3065a81141aad9f129
     * project : {"key":"myproject","name":"My Project","url":"https://mycompany.com/sonarqube/dashboard?id=myproject"}
     * properties : {}
     * qualityGate : {"conditions":[{"errorThreshold":"1","metric":"new_security_rating","onLeakPeriod":true,"operator":"GREATER_THAN","status":"OK","value":"1"},{"errorThreshold":"1","metric":"new_reliability_rating","onLeakPeriod":true,"operator":"GREATER_THAN","status":"OK","value":"1"},{"errorThreshold":"1","metric":"new_maintainability_rating","onLeakPeriod":true,"operator":"GREATER_THAN","status":"OK","value":"1"},{"errorThreshold":"80","metric":"new_coverage","onLeakPeriod":true,"operator":"LESS_THAN","status":"NO_VALUE"}],"name":"SonarQube way","status":"OK"}
     */

    private String serverUrl;
    private String taskId;
    private String status;
    private String analysedAt;
    private String revision;
    private ProjectBean project;
    private Map<String, String> properties;
    private QualityGateBean qualityGate;

    @Getter
    @Setter
    @ToString
    public static class ProjectBean {
        /**
         * key : myproject
         * name : My Project
         * url : https://mycompany.com/sonarqube/dashboard?id=myproject
         */

        private String key;
        private String name;
        private String url;

    }

    @Getter
    @Setter
    @ToString
    public static class QualityGateBean {
        /**
         * conditions : [{"errorThreshold":"1","metric":"new_security_rating","onLeakPeriod":true,"operator":"GREATER_THAN","status":"OK","value":"1"},{"errorThreshold":"1","metric":"new_reliability_rating","onLeakPeriod":true,"operator":"GREATER_THAN","status":"OK","value":"1"},{"errorThreshold":"1","metric":"new_maintainability_rating","onLeakPeriod":true,"operator":"GREATER_THAN","status":"OK","value":"1"},{"errorThreshold":"80","metric":"new_coverage","onLeakPeriod":true,"operator":"LESS_THAN","status":"NO_VALUE"}]
         * name : SonarQube way
         * status : OK
         */

        private String name;
        private String status;
        private List<ConditionsBean> conditions;

        @Getter
        @Setter
        @ToString
        public static class ConditionsBean {
            /**
             * errorThreshold : 1
             * metric : new_security_rating
             * onLeakPeriod : true
             * operator : GREATER_THAN
             * status : OK
             * value : 1
             */

            private String errorThreshold;
            private String metric;
            private boolean onLeakPeriod;
            private String operator;
            private String status;
            private String value;

        }
    }
}
