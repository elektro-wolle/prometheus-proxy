/*
 *  Copyright 2017, Paul Ambrose All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.prometheus.agent;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.prometheus.Agent;
import io.prometheus.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentClientInterceptor
    implements ClientInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(AgentClientInterceptor.class);

  private final Agent agent;

  public AgentClientInterceptor(Agent agent) {
    this.agent = agent;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method,
                                                             final CallOptions callOptions,
                                                             final Channel next) {
    // final String methodName = method.getFullMethodName();
    // logger.info("Intercepting {}", methodName);
    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(this.agent.getChannel().newCall(method,
                                                                                                            callOptions)) {
      @Override
      public void start(final Listener<RespT> responseListener, final Metadata headers) {
        super.start(
            new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
              @Override
              public void onHeaders(Metadata headers) {
                // Grab agent_id from headers if not already assigned
                if (agent.getAgentId() == null) {
                  final String agentId = headers.get(Metadata.Key.of(Proxy.AGENT_ID, Metadata.ASCII_STRING_MARSHALLER));
                  if (agentId != null) {
                    agent.setAgentId(agentId);
                    logger.info("Assigned agentId to {}", agent);
                  }
                  else {
                    logger.error("Headers missing AGENT_ID key");
                  }
                }
                super.onHeaders(headers);
              }
            },
            headers);
      }
    };
  }
}