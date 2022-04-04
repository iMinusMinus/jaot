package org.springframework.aot.context.bootstrap.generator.bean.descriptor;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Executable;
import java.util.List;

/**
 * refer org.springframework.aot.context.bootstrap.generator.bean.descriptor.DefaultBeanInstanceDescriptorFactory
 */
public class JakartaBeanInstanceDescriptorFactory extends DefaultBeanInstanceDescriptorFactory {

    private final BeanInstanceExecutableSupplier instanceCreatorSupplier;

    private final InjectionPointsSupplier injectionPointsSupplier;

    private final PropertiesSupplier propertiesSupplier;

    public JakartaBeanInstanceDescriptorFactory(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
        instanceCreatorSupplier = new BeanInstanceExecutableSupplier(beanFactory);
        injectionPointsSupplier = new JakartaInjectionPointsSupplier(beanFactory.getBeanClassLoader());
        propertiesSupplier = new PropertiesSupplier();
    }

    @Override
    public BeanInstanceDescriptor create(BeanDefinition beanDefinition) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        Executable instanceCreator = this.instanceCreatorSupplier.detectBeanInstanceExecutable(beanDefinition);
        if (instanceCreator != null) {
            Class<?> beanType = ClassUtils.getUserClass(beanDefinition.getResolvableType().toClass());
            List<BeanInstanceDescriptor.MemberDescriptor<?>> injectionPoints = this.injectionPointsSupplier.detectInjectionPoints(beanType);
            List<BeanInstanceDescriptor.PropertyDescriptor> properties = this.propertiesSupplier.detectProperties(beanDefinition);
            return BeanInstanceDescriptor.of(beanDefinition.getResolvableType())
                    .withInstanceCreator(instanceCreator).withInjectionPoints(injectionPoints)
                    .withProperties(properties).build();
        }
        return null;
    }

}
