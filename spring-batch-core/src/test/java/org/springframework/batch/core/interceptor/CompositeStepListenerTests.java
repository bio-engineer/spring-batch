/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.interceptor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.batch.core.domain.StepExecution;
import org.springframework.batch.core.domain.StepListener;
import org.springframework.batch.core.domain.StepSupport;
import org.springframework.batch.repeat.ExitStatus;

/**
 * @author Dave Syer
 * 
 */
public class CompositeStepListenerTests extends TestCase {

	private CompositeStepListener listener = new CompositeStepListener();

	private List list = new ArrayList();

	/**
	 * Test method for
	 * {@link org.springframework.batch.core.interceptor.CompositeStepListener#setListeners(org.springframework.batch.core.domain.StepListener[])}.
	 */
	public void testSetListeners() {
		listener.setListeners(new StepListener[] { new StepListenerSupport() {
			public ExitStatus afterStep() {
				list.add("fail");
				return ExitStatus.FAILED;
			}
		}, new StepListenerSupport() {
			public ExitStatus afterStep() {
				list.add("continue");
				return ExitStatus.CONTINUABLE;
			}
		} });
		assertFalse(listener.afterStep().isContinuable());
		assertEquals(2, list.size());
	}

	/**
	 * Test method for
	 * {@link org.springframework.batch.core.interceptor.CompositeStepListener#setListener(org.springframework.batch.core.domain.StepListener)}.
	 */
	public void testSetListener() {
		listener.register(new StepListenerSupport() {
			public ExitStatus afterStep() {
				list.add("fail");
				return ExitStatus.FAILED;
			}
		});
		assertFalse(listener.afterStep().isContinuable());
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link org.springframework.batch.core.interceptor.CompositeStepListener#beforeStep(StepExecution)}.
	 */
	public void testOpen() {
		listener.register(new StepListenerSupport() {
			public void beforeStep(StepExecution stepExecution) {
				list.add("foo");
			}
		});
		listener.beforeStep(new StepExecution(new StepSupport("foo"), null));
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link org.springframework.batch.core.interceptor.CompositeStepListener#beforeStep(StepExecution)}.
	 */
	public void testOnError() {
		listener.register(new StepListenerSupport() {
			public ExitStatus onErrorInStep(Throwable e) {
				list.add("foo");
				return null;
			}
		});
		listener.onErrorInStep(new RuntimeException());
		assertEquals(1, list.size());
	}

}
