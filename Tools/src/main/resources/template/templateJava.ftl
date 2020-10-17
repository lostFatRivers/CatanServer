package ${classPackage};

<#if importList??>
    <#list importList as eachPackage>
import ${eachPackage};
    </#list>
</#if>

/**
 * ==================================
 * = auto generated, do not modify! =
 * ==================================
 */
public class ${className} {
    <#if fieldModelList??>
        <#list fieldModelList as fModel>
            <#if (fModel.fieldDesc)??>
    /** ${fModel.fieldDesc} */
            </#if>
    private ${fModel.fieldType} ${fModel.fieldName};
    
        </#list>
    </#if>
    <#if fieldModelList??>
        <#list fieldModelList as fModel>
    public ${fModel.fieldType} get${fModel.fieldName?cap_first}() {
        return ${fModel.fieldName};
    }

    public void set${fModel.fieldName?cap_first}(${fModel.fieldType} ${fModel.fieldName}) {
        this.${fModel.fieldName} = ${fModel.fieldName};
    }

        </#list>
    </#if>
}