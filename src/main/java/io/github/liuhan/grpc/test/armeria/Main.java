/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.liuhan.grpc.test.armeria;

import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.grpc.GrpcService;
import io.github.liuhan.grpc.test.HelloServiceHandler;

import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ServerBuilder sb = Server.builder();
        sb.service(GrpcService.builder()
            .addService(new HelloServiceHandler())
                .useBlockingTaskExecutor(true)
            .build());
        sb.port(8888, SessionProtocol.HTTP);
        sb.blockingTaskExecutor(256);
        Server server = sb.build();
        server.start();
        new LinkedBlockingQueue<Boolean>().take();
    }
}
