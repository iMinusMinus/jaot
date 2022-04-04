package org.springframework.aot.context.bootstrap.generator.bean;

import org.springframework.aot.context.bootstrap.generator.bean.descriptor.BeanInstanceDescriptor;
import org.springframework.aot.context.bootstrap.generator.bean.descriptor.BeanInstanceDescriptorFactory;
import org.springframework.aot.context.bootstrap.generator.bean.descriptor.JakartaBeanInstanceDescriptorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * refer org.springframework.aot.context.bootstrap.generator.bean.DefaultBeanRegistrationWriterSupplier
 * 优先级较高，覆盖DefaultBeanRegistrationWriterSupplier行为！
 */
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class JakartaBeanRegistrationWriterSupplier implements BeanRegistrationWriterSupplier, BeanFactoryAware {

    private BeanInstanceDescriptorFactory beanInstanceDescriptorFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanInstanceDescriptorFactory = new JakartaBeanInstanceDescriptorFactory((ConfigurableBeanFactory) beanFactory);
    }

    @Override
    public final BeanRegistrationWriter get(String beanName, BeanDefinition beanDefinition) {
        if (!isCandidate(beanName, beanDefinition)) {
            return null;
        }
        BeanInstanceDescriptor beanInstanceDescriptor = resolveBeanInstanceDescriptor(beanDefinition);
        return (beanInstanceDescriptor != null) ? createInstance(beanName, beanDefinition, beanInstanceDescriptor) : null;
    }

    /**
     * Check if the specified bean definition is a candidate for this instance.
     * @param beanName the bean name
     * @param beanDefinition the bean definition
     * @return {@code true} if this instance should provider a
     * {@link BeanRegistrationWriter} for this bean definition
     */
    protected boolean isCandidate(String beanName, BeanDefinition beanDefinition) {
        return true;
    }

    protected BeanRegistrationWriter createInstance(String beanName, BeanDefinition beanDefinition,
                                                    BeanInstanceDescriptor beanInstanceDescriptor) {
        return new DefaultBeanRegistrationWriter(beanName, beanDefinition, beanInstanceDescriptor,
                initializeOptions().build());
    }

    protected BeanRegistrationWriterOptions.Builder initializeOptions() {
        return BeanRegistrationWriterOptions.builder().withWriterFactory(this::get);
    }

    private BeanInstanceDescriptor resolveBeanInstanceDescriptor(BeanDefinition beanDefinition) {
        return this.beanInstanceDescriptorFactory.create(beanDefinition);
    }
}
