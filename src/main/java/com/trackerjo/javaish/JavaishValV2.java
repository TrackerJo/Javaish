package com.trackerjo.javaish;

import java.util.List;

public sealed interface JavaishValV2 permits JavaishString, JavaishBool, JavaishInt, JavaishFloat, JavaishBooleanList {}

record JavaishString(String value) implements JavaishValV2 {}
record JavaishBool(Boolean value) implements JavaishValV2 {}
record JavaishInt(Integer value) implements JavaishValV2 {}
record JavaishFloat(Float value) implements JavaishValV2 {}
record JavaishBooleanList(List<Boolean> value) implements JavaishValV2 {}
