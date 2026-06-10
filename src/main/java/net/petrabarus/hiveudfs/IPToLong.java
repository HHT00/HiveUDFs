package net.petrabarus.hiveudfs;
import net.petrabarus.hiveudfs.helpers.InetAddrHelper;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

@UDFType(deterministic = true) // 用来描述给函数是确定的，在hive优化器中可以调优（谓词下推，常量折叠）
@Description(
        name="IpToLong", //在hive中的函数名
        value="_FUNC_(str)- return ip address in long form ipString ip",//简短描述
        extended="Example:\n"
                + " > SELECT _FUNC_(\"1.1.1.1\") FROM table"
                + " > 16843009")//详细描述
public class IPToLong extends GenericUDF {
        private ObjectInspectorConverters.Converter converter; //转化器
        private final LongWritable result = new LongWritable();//保存结果

        @Override
        public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
                if(arguments.length != 1){
                        throw new UDFArgumentLengthException("_FUNC_ expect only 1 argument");
                }
                if(arguments[0].getCategory() != ObjectInspector.Category.PRIMITIVE){
                        throw new UDFArgumentTypeException(0,"throw new UDFArgumentTypeException(0,\n" +
                                "                                \"A string argument was expected but an argument of type \" + argument.getTypeName()\n" +
                                "                                + \" was given.\");");
                }
                if(((PrimitiveObjectInspector)arguments[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING){
                        throw new UDFArgumentTypeException(0,"throw new UDFArgumentTypeException(0,\n" +
                                "                                \"A string argument was expected but an argument of type "+ arguments[0].getTypeName() +
                                "                                + \" was given.\");");
                }

                converter =  ObjectInspectorConverters.getConverter(arguments[0], PrimitiveObjectInspectorFactory.writableStringObjectInspector);
                return PrimitiveObjectInspectorFactory.writableLongObjectInspector;//声明udf返回值类型
        }

        @Override
        public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

                if(deferredObjects[0].get() ==null || deferredObjects.length != 1){
                        return  null;
                }
                Text t = (Text)converter.convert(deferredObjects[0].get());//hivedata->hadoop(Text)
                result.set(InetAddrHelper.IPToLong(t.toString()));//hadoop(Text)-> java String ->java long-> hadoop(LongWritable)
                return result;
        }

        @Override
        public String getDisplayString(String[] strings) {//explan 描述函数

                return "_FUNC_(" + strings[0] + ")";
        }
}
