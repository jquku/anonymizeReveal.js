import java.io.IOException;

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import java.nio.charset.Charset;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased.Order;
import org.deidentifier.arx.AttributeType.Hierarchy;

class LAnonymizer {

	public static void main(String[] args) throws IOException {

			Data data1 = Data.create("students.csv", Charset.defaultCharset(), ',');
			Data data2 = Data.create("tracked_sessions.csv",
					Charset.defaultCharset(), ',');

			data1.getDefinition().setDataType("user_token", DataType.INTEGER);
			data1.getDefinition().setAttributeType("user_token",
					AttributeType.INSENSITIVE_ATTRIBUTE);

			data2.getDefinition().setDataType("student_id", DataType.INTEGER);
			data2.getDefinition().setAttributeType("student_id",
					AttributeType.INSENSITIVE_ATTRIBUTE);

			data1.getDefinition().setDataType("id", DataType.INTEGER);
			data1.getDefinition().setAttributeType("id",
					AttributeType.INSENSITIVE_ATTRIBUTE);

			data2.getDefinition().setDataType("id", DataType.INTEGER);
			data2.getDefinition().setAttributeType("id",
					AttributeType.INSENSITIVE_ATTRIBUTE);

			data1.getDefinition().setDataType("created_at", DataType.createDate(
					"yyyy-MM-dd HH:mm:ss.SSSSSS"));
			data1.getDefinition().setDataType("updated_at", DataType.createDate(
					"yyyy-MM-dd HH:mm:ss.SSSSSS"));

			data2.getDefinition().setDataType("created_at", DataType.createDate(
					"yyyy-MM-dd HH:mm:ss.SSSSSS"));
			data2.getDefinition().setDataType("updated_at", DataType.createDate(
					"yyyy-MM-dd HH:mm:ss.SSSSSS"));


			HierarchyBuilderRedactionBased<?> builder =
					HierarchyBuilderRedactionBased.create(Order.RIGHT_TO_LEFT,
					Order.RIGHT_TO_LEFT, ' ', '*');

			data1.getDefinition().setAttributeType("created_at", builder);
			data1.getDefinition().setAttributeType("updated_at", builder);

			data2.getDefinition().setAttributeType("created_at", builder);
			data2.getDefinition().setAttributeType("updated_at", builder);

			//instance of the anonymizer
			ARXAnonymizer anonymizer = new ARXAnonymizer();

			// ARX configuration
			ARXConfiguration config = ARXConfiguration.create();
			config.addPrivacyModel(new KAnonymity(2));
			config.setSuppressionLimit(1d);
			config.setHeuristicSearchStepLimit(1000);
			config.setHeuristicSearchEnabled(true);

			ARXResult result1 = anonymizer.anonymize(data1, config);
			ARXResult result2 = anonymizer.anonymize(data2, config);

			//obtain various data representations
			DataHandle optimal1 = result1.getOutput();
			DataHandle optimal2 = result2.getOutput();

			optimal1.save("students_new.csv", ',');
			optimal2.save("tracked_sessions_new.csv", ',');
	}
}
