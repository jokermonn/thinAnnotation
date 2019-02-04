package com.joker.buildsrc

import proguard.obfuscate.MappingProcessor

class AbstractMappingProcessor implements MappingProcessor {
    @Override
    boolean processClassMapping(String className, String newClassName) {
        return false
    }

    @Override
    void processFieldMapping(String className, String fieldType, String fieldName, String newClassName, String newFieldName) {

    }

    @Override
    void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newClassName, int newFirstLineNumber, int newLastLineNumber, String newMethodName) {

    }
}
