// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.agent.api.to;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cloud.network.as.AutoScalePolicy;
import com.cloud.network.as.AutoScaleVmGroup;
import com.cloud.network.as.AutoScaleVmProfile;
import com.cloud.network.as.Condition;
import com.cloud.network.as.Counter;
import com.cloud.network.lb.LoadBalancingRule.LbAutoScalePolicy;
import com.cloud.network.lb.LoadBalancingRule.LbAutoScaleVmGroup;
import com.cloud.network.lb.LoadBalancingRule.LbAutoScaleVmProfile;
import com.cloud.network.lb.LoadBalancingRule.LbCondition;
import com.cloud.network.lb.LoadBalancingRule.LbDestination;
import com.cloud.network.lb.LoadBalancingRule.LbStickinessPolicy;
import com.cloud.utils.Pair;


public class LoadBalancerTO {
    Long id;
    String srcIp;
    int srcPort;
    String protocol;
    String algorithm;
    boolean revoked;
    boolean alreadyAdded;
    DestinationTO[] destinations;
    private StickinessPolicyTO[] stickinessPolicies;
    private AutoScaleVmGroupTO autoScaleVmGroupTO;
    final static int MAX_STICKINESS_POLICIES = 1;

    public LoadBalancerTO (Long id, String srcIp, int srcPort, String protocol, String algorithm, boolean revoked, boolean alreadyAdded, List<LbDestination> destinations) {
        if(destinations == null) { // for autoscaleconfig destinations will be null;
            destinations = new ArrayList<LbDestination>();
        }
        this.id = id;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.protocol = protocol;
        this.algorithm = algorithm;
        this.revoked = revoked;
        this.alreadyAdded = alreadyAdded;
        this.destinations = new DestinationTO[destinations.size()];
        this.stickinessPolicies = null;
        int i = 0;
        for (LbDestination destination : destinations) {
            this.destinations[i++] = new DestinationTO(destination.getIpAddress(), destination.getDestinationPortStart(), destination.isRevoked(), false);
        }
    }

    public LoadBalancerTO (Long id, String srcIp, int srcPort, String protocol, String algorithm, boolean revoked, boolean alreadyAdded, List<LbDestination> arg_destinations, List<LbStickinessPolicy> stickinessPolicies) {
        this(id, srcIp, srcPort, protocol, algorithm, revoked, alreadyAdded, arg_destinations);
        this.stickinessPolicies = null;
        if (stickinessPolicies != null && stickinessPolicies.size()>0) {
            this.stickinessPolicies = new StickinessPolicyTO[MAX_STICKINESS_POLICIES];
            int index = 0;
            for (LbStickinessPolicy stickinesspolicy : stickinessPolicies) {
                if (!stickinesspolicy.isRevoked()) {
                    this.stickinessPolicies[index] = new StickinessPolicyTO(stickinesspolicy.getMethodName(), stickinesspolicy.getParams());
                    index++;
                    if (index == MAX_STICKINESS_POLICIES) break;
                }
            }
            if (index == 0) this.stickinessPolicies = null;
        }
    }


    protected LoadBalancerTO() {
    }


    public Long getId() {
        return id;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isAlreadyAdded() {
        return alreadyAdded;
    }

    public StickinessPolicyTO[] getStickinessPolicies() {
        return stickinessPolicies;
    }

    public DestinationTO[] getDestinations() {
        return destinations;
    }

    public AutoScaleVmGroupTO getAutoScaleVmGroupTO() {
        return autoScaleVmGroupTO;
    }

    public void setAutoScaleVmGroupTO(AutoScaleVmGroupTO autoScaleVmGroupTO) {
        this.autoScaleVmGroupTO = autoScaleVmGroupTO;
    }

    public boolean isAutoScaleVmGroupTO() {
        return this.autoScaleVmGroupTO != null;
    }

    public static class StickinessPolicyTO {
        private String _methodName;
        private List<Pair<String, String>> _paramsList;

        public String getMethodName() {
            return _methodName;
        }

        public List<Pair<String, String>> getParams() {
            return _paramsList;
        }

        public StickinessPolicyTO(String methodName, List<Pair<String, String>> paramsList) {
            this._methodName = methodName;
            this._paramsList = paramsList;
        }
    }

    public static class DestinationTO {
        String destIp;
        int destPort;
        boolean revoked;
        boolean alreadyAdded;
        public DestinationTO(String destIp, int destPort, boolean revoked, boolean alreadyAdded) {
            this.destIp = destIp;
            this.destPort = destPort;
            this.revoked = revoked;
            this.alreadyAdded = alreadyAdded;
        }

        protected DestinationTO() {
        }

        public String getDestIp() {
            return destIp;
        }

        public int getDestPort() {
            return destPort;
        }

        public boolean isRevoked() {
            return revoked;
        }

        public boolean isAlreadyAdded() {
            return alreadyAdded;
        }
    }
    public static class CounterTO implements Serializable{
        private final String name;
        private final String source;
        private final String value;

        public CounterTO(String name, String source, String value) {
            this.name = name;
            this.source = source;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getSource() {
            return source;
        }

        public String getValue() {
            return value;
        }
    }

    public static class ConditionTO implements Serializable{
        private final long threshold;
        private final String relationalOperator;
        private final CounterTO counter;

        public ConditionTO(long threshold, String relationalOperator, CounterTO counter)
        {
            this.threshold = threshold;
            this.relationalOperator = relationalOperator;
            this.counter = counter;
        }

        public long getThreshold() {
            return threshold;
        }

        public String getRelationalOperator() {
            return relationalOperator;
        }

        public CounterTO getCounter() {
            return counter;
        }
    }

    public static class AutoScalePolicyTO implements Serializable{
        private final long id;
        private final int duration;
        private final int quietTime;
        private String action;
        boolean revoked;
        private final List<ConditionTO> conditions;

        public AutoScalePolicyTO(long id, int duration, int quietTime, String action, List<ConditionTO> conditions, boolean revoked) {
            this.id = id;
            this.duration = duration;
            this.quietTime = quietTime;
            this.conditions = conditions;
            this.action = action;
            this.revoked = revoked;
        }

        public long getId() {
            return id;
        }

        public int getDuration() {
            return duration;
        }

        public int getQuietTime() {
            return quietTime;
        }

        public String getAction() {
            return action;
        }

        public boolean isRevoked() {
            return revoked;
        }

        public List<ConditionTO> getConditions() {
            return conditions;
        }
    }

    public static class AutoScaleVmProfileTO implements Serializable{
        private final Long zoneId;
        private final Long domainId;
        private final Long serviceOfferingId;
        private final Long templateId;
        private final String otherDeployParams;
        private final String snmpCommunity;
        private final Integer snmpPort;
        private final Integer destroyVmGraceperiod;
        private final String cloudStackApiUrl;
        private final String autoScaleUserApiKey;
        private final String autoScaleUserSecretKey;

        public AutoScaleVmProfileTO(Long zoneId, Long domainId, String cloudStackApiUrl, String autoScaleUserApiKey, String autoScaleUserSecretKey, Long serviceOfferingId, Long templateId,
                String otherDeployParams, String snmpCommunity, Integer snmpPort, Integer destroyVmGraceperiod) {
            this.zoneId = zoneId;
            this.domainId = domainId;
            this.serviceOfferingId = serviceOfferingId;
            this.templateId = templateId;
            this.otherDeployParams = otherDeployParams;
            this.snmpCommunity = snmpCommunity;
            this.snmpPort = snmpPort;
            this.destroyVmGraceperiod = destroyVmGraceperiod;
            this.cloudStackApiUrl = cloudStackApiUrl;
            this.autoScaleUserApiKey = autoScaleUserApiKey;
            this.autoScaleUserSecretKey = autoScaleUserSecretKey;
        }

        public Long getZoneId() {
            return zoneId;
        }

        public Long getDomainId() {
            return domainId;
        }

        public Long getServiceOfferingId() {
            return serviceOfferingId;
        }

        public Long getTemplateId() {
            return templateId;
        }

        public String getOtherDeployParams() {
            return otherDeployParams;
        }

        public String getSnmpCommunity() {
            return snmpCommunity;
        }

        public Integer getSnmpPort() {
            return snmpPort;
        }

        public Integer getDestroyVmGraceperiod() {
            return destroyVmGraceperiod;
        }

        public String getCloudStackApiUrl() {
            return cloudStackApiUrl;
        }

        public String getAutoScaleUserApiKey() {
            return autoScaleUserApiKey;
        }

        public String getAutoScaleUserSecretKey() {
            return autoScaleUserSecretKey;
        }
    }

    public static class AutoScaleVmGroupTO implements Serializable{
        private final int minMembers;
        private final int maxMembers;
        private final int memberPort;
        private final int interval;
        private final List<AutoScalePolicyTO> policies;
        private final AutoScaleVmProfileTO profile;
        private final String state;

        AutoScaleVmGroupTO(int minMembers, int maxMembers, int memberPort, int interval, List<AutoScalePolicyTO> policies, AutoScaleVmProfileTO profile, String state)
        {
            this.minMembers = minMembers;
            this.maxMembers = maxMembers;
            this.memberPort = memberPort;
            this.interval = interval;
            this.policies = policies;
            this.profile = profile;
            this.state = state;
        }

        public int getMinMembers() {
            return minMembers;
        }

        public int getMaxMembers() {
            return maxMembers;
        }

        public int getMemberPort() {
            return memberPort;
        }

        public int getInterval() {
            return interval;
        }

        public List<AutoScalePolicyTO> getPolicies() {
            return policies;
        }

        public AutoScaleVmProfileTO getProfile() {
            return profile;
        }

        public String getState() {
            return state;
        }
    }

    public void setAutoScaleVmGroup(LbAutoScaleVmGroup lbAutoScaleVmGroup)
    {
        List<LbAutoScalePolicy> lbAutoScalePolicies = lbAutoScaleVmGroup.getPolicies();
        List<AutoScalePolicyTO> autoScalePolicyTOs = new ArrayList<AutoScalePolicyTO>(lbAutoScalePolicies.size());
        for (LbAutoScalePolicy lbAutoScalePolicy : lbAutoScalePolicies) {
            List<LbCondition> lbConditions = lbAutoScalePolicy.getConditions();
            List<ConditionTO> conditionTOs = new ArrayList<ConditionTO>(lbConditions.size());
            for (LbCondition lbCondition : lbConditions) {
                Counter counter = lbCondition.getCounter();
                CounterTO counterTO = new CounterTO(counter.getName(), counter.getSource().toString(), "" + counter.getValue());
                Condition condition = lbCondition.getCondition();
                ConditionTO conditionTO = new ConditionTO(condition.getThreshold(), condition.getRelationalOperator().toString(), counterTO);
                conditionTOs.add(conditionTO);
            }
            AutoScalePolicy autoScalePolicy = lbAutoScalePolicy.getPolicy();
            autoScalePolicyTOs.add(new AutoScalePolicyTO(autoScalePolicy.getId(), autoScalePolicy.getDuration(),
                    autoScalePolicy.getQuietTime(), autoScalePolicy.getAction(),
                    conditionTOs, lbAutoScalePolicy.isRevoked()));
        }
        LbAutoScaleVmProfile lbAutoScaleVmProfile = lbAutoScaleVmGroup.getProfile();
        AutoScaleVmProfile autoScaleVmProfile = lbAutoScaleVmProfile.getProfile();

        AutoScaleVmProfileTO autoScaleVmProfileTO = new AutoScaleVmProfileTO(autoScaleVmProfile.getZoneId(), autoScaleVmProfile.getDomainId(),
                lbAutoScaleVmProfile.getCsUrl(), lbAutoScaleVmProfile.getAutoScaleUserApiKey(), lbAutoScaleVmProfile.getAutoScaleUserSecretKey(),
                autoScaleVmProfile.getServiceOfferingId(), autoScaleVmProfile.getTemplateId(), autoScaleVmProfile.getOtherDeployParams(),
                autoScaleVmProfile.getSnmpCommunity(), autoScaleVmProfile.getSnmpPort(), autoScaleVmProfile.getDestroyVmGraceperiod());

        AutoScaleVmGroup autoScaleVmGroup = lbAutoScaleVmGroup.getVmGroup();
        autoScaleVmGroupTO = new AutoScaleVmGroupTO(autoScaleVmGroup.getMinMembers(), autoScaleVmGroup.getMaxMembers(), autoScaleVmGroup.getMemberPort(),
                autoScaleVmGroup.getInterval(), autoScalePolicyTOs, autoScaleVmProfileTO, autoScaleVmGroup.getState());
    }

}
