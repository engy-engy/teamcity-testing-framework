package com.example.teamcity.api.generators;

import com.example.teamcity.api.annotations.Dependent;
import com.example.teamcity.api.annotations.Random;
import com.example.teamcity.api.annotations.Parameterizable;
import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.TestData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
/**
 * Основной метод генерации тестовых данных.
 *
 * Если у поля аннотация Optional, оно пропускается, иначе:
 *
 * 1) если у поля аннотация Parameterizable, и в метод были переданы параметры, то поочередно (по мере встречи полей с
 *     этой аннотацией) устанавливаются переданные параметры. То есть, если по ходу генерации было пройдено 4 поля с
 *     аннотацией Parameterizable, но параметров в метод было передано 3, то значения будут установлены только у первых
 *     трех встретившихся элементов в порядке их передачи в метод. Поэтому также важно следить за порядком полей
 *     в @Data классе;
 *
 * 2) иначе, если у поля аннотация Random и это строка, оно заполняется рандомными данными;
 *
 * 3) иначе, если поле - наследник класса BaseModel, то оно генерируется, рекурсивно отправляясь в новый метод generate;
 *
 * 4) иначе, если поле - List, у которого generic type - наследник класса BaseModel, то оно устанавливается списком
 *     из одного элемента, который генерируется, рекурсивно отправляясь в новый метод generate.
 *
 * Параметр generatedModels передается, когда генерируется несколько сущностей в цикле, и содержит в себе
 * сгенерированные на предыдущих шагах сущности. Позволяет при генерации сложной сущности, которая своим полем содержит
 * другую сущность, сгенерированную на предыдущем шаге, установить ее, а не генерировать новую. Данная логика
 * применяется только для пунктов 3 и 4. Например, если был сгенерирован NewProjectDescription, то передав его
 * параметром generatedModels при генерации BuildType, он будет переиспользоваться при установке
 * поля NewProjectDescription project, вместо генерации нового.
 */
public class TestDataGenerator {
    private TestDataGenerator() {}

    /**
     * Отвечает за генерацию тестовых данных на основе классов моделей (BaseModel). Его основной функционал:
     *
     * 	•	Генерация случайных данных для полей моделей.
     * 	•	Поддержка параметризуемых полей через аннотацию @Parameterizable.
     * 	•	Обработка списков (коллекций) объектов, которые могут быть наследниками BaseModel.
     * 	•	Рекурсивная генерация объектов, если у них есть поля, которые также являются моделями данных.
     * @param generatedModels
     * @param generatorClass
     * @param parameters
     * @return
     * @param <T>
     */
    public static <T extends BaseModel> T generate(List<BaseModel> generatedModels, Class<T> generatorClass,
                                                   Object... parameters) {
        try {
            var instance = generatorClass.getDeclaredConstructor().newInstance();
            for (var field : generatorClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Optional.class)) {
                    var generatedClass = generatedModels.stream().filter(m
                            -> m.getClass().equals(field.getType())).findFirst();
                    if (field.isAnnotationPresent(Parameterizable.class) && parameters.length > 0) {
                        field.set(instance, parameters[0]);
                        parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
                    } else if (field.isAnnotationPresent(Dependent.class) && generatedModels.stream().anyMatch(m
                            -> m.getClass().equals(field.getAnnotation(Dependent.class).relatedClass()))) {
                        var relatedClass = field.getAnnotation(Dependent.class).relatedClass();
                        var generatedRelatedModel = generatedModels.stream().filter(m
                                -> m.getClass().equals(relatedClass)).findFirst();
                        if (generatedRelatedModel.isPresent()) {
                            var relatedField = relatedClass.getDeclaredField(field.getName());
                            relatedField.setAccessible(true);
                            var relatedValue = relatedField.get(generatedRelatedModel.get());
                            relatedField.setAccessible(false);
                            field.set(instance, relatedValue);
                        }
                    } else if (field.isAnnotationPresent(Random.class) && String.class.equals(field.getType())) {
                        field.set(instance, RandomData.getString());
                    } else if (field.isAnnotationPresent(Random.class) && long.class.equals(field.getType())) {
                        // Генерация случайного long
                        field.set(instance, RandomData.getLong());
                    } else if (BaseModel.class.isAssignableFrom(field.getType())) {
                        var finalParameters = parameters;
                        field.set(instance, generatedClass.orElseGet(() -> generate(
                                generatedModels, field.getType().asSubclass(BaseModel.class), finalParameters)));
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        if (field.getGenericType() instanceof ParameterizedType pt) {
                            var typeClass = (Class<?>) pt.getActualTypeArguments()[0];
                            if (BaseModel.class.isAssignableFrom(typeClass)) {
                                var finalParameters = parameters;
                                field.set(instance, generatedClass.map(List::of).orElseGet(() -> List.of(generate(
                                        generatedModels, typeClass.asSubclass(BaseModel.class), finalParameters))));
                            }
                        }
                    }
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException | NoSuchFieldException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

    public static TestData generate() {
        // Идем по всем полям TestData и для каждого кто наследник BaseModel
        // вызываем generate с передачей уже сгенерированных сущностей
        try {
            var instance = TestData.class.getDeclaredConstructor().newInstance();
            var generatedModels = new ArrayList<BaseModel>();
            for (var field: TestData.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (BaseModel.class.isAssignableFrom(field.getType())) {
                    var generatedModel = generate(generatedModels, field.getType().asSubclass(BaseModel.class));
                    field.set(instance,generatedModel);
                    generatedModels.add(generatedModel);
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data\n", e);
        }
    }

    // Метод, чтобы сгенерировать одну сущность. Передает пустой параметр generatedModels
    public static <T extends BaseModel> T generate(Class<T> generatorClass, Object... parameters) {
        return generate(Collections.emptyList(), generatorClass, parameters);
    }
}
