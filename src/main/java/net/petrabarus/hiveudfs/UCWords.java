package net.petrabarus.hiveudfs;

import org.apache.commons.lang.WordUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;


public class UCWords extends GenericUDF {

        private ObjectInspectorConverters.Converter converter;
        private Text result = new Text();

        /**
         * Initialize this UDF.
         *
         * This will be called once and only once per GenericUDF instance.
         *全局只调用一次
         * @param arguments The ObjectInspector for the arguments 参数是ObjectInspector类型
         * @throws UDFArgumentException Thrown when arguments have wrong types,
         * wrong length, etc.
         * 抛出异常有类型，长度等等
         * @return The ObjectInspector for the return value
         * 返回ObjectInspector类型
         */
        @Override
        public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
                //判断参数长度，抛出长度异常
                if(arguments.length != 1){
                        throw new UDFArgumentLengthException("The  number of _FUNC_ argument not is once");
                }
                //获取数组第一个值
                ObjectInspector argument = arguments[0];
                //判断类型是不是原始数据，抛出类型异常
                if(argument.getCategory() != ObjectInspector.Category.PRIMITIVE){
                        throw new UDFArgumentTypeException(0,"throw new UDFArgumentTypeException(0,\n" +
                                "                                \"A string argument was expected but an argument of type "+ arguments[0].getTypeName() +
                                "                                + \" was given.\");");
                }
                //转化为子类PrimitiveObjectInspector
                PrimitiveObjectInspector.PrimitiveCategory primitiveCategory = ((PrimitiveObjectInspector)argument).getPrimitiveCategory();
                //判断子类型为STRING
                if(primitiveCategory != PrimitiveObjectInspector.PrimitiveCategory.STRING){
                        throw new UDFArgumentTypeException(0,"throw new UDFArgumentTypeException(0,\n" +
                                "                                \"A string argument was expected but an argument of type "+ arguments[0].getTypeName() +
                                "                                + \" was given.\");");
                }
                //转化器创建转化规则，原始类型转化为writableString
                converter=ObjectInspectorConverters.getConverter(argument,PrimitiveObjectInspectorFactory.writableStringObjectInspector);

                //声明返回类型为writableStringObjectInspector
                return PrimitiveObjectInspectorFactory.writableStringObjectInspector;

        }

        /**
         * Evaluate the UDF with the arguments.
         *
         * @param arguments The arguments as DeferedObject, use
         * DeferedObject.get() to get the actual argument Object. The Objects
         * can be inspected by the ObjectInspectors passed in the initialize
         * call.
         * @return The return value.
         */
        @Override
        public Object evaluate(DeferredObject[] arguments) throws HiveException {

                if (arguments[0].get() == null |arguments.length != 1) {
                        return null;
                }
                String str = ((Text) converter.convert(arguments[0].get())).toString();
                result.set(WordUtils.capitalize(str));
                return result;
        }

        /**
         * Get the String to be displayed in explain.
         *
         * @return The display string.
         */
        @Override
        public String getDisplayString(String[] strings) {

                return "_FUNC_(" + strings[0] + ")";
        }
}
