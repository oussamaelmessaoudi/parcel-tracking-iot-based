{
 "cells": [
  {
   "cell_type": "code",
   "execution_count": 1,
   "id": "6dac0b74-3eb1-4634-a86e-bc18dc709b76",
   "metadata": {},
   "outputs": [
    {
     "name": "stdout",
     "output_type": "stream",
     "text": [
      "Spark version: 3.5.7\n",
      "Starting streams... waiting for termination.\n"
     ]
    },
    {
     "ename": "StreamingQueryException",
     "evalue": "[STREAM_FAILED] Query [id = 90f5c898-d3c9-415d-b3c9-f85dcbdd113c, runId = a6d892f6-886a-4a69-8da7-f57cf379e7a0] terminated with exception: 'boolean org.apache.hadoop.io.nativeio.NativeIO$Windows.access0(java.lang.String, int)'",
     "output_type": "error",
     "traceback": [
      "\u001b[31m---------------------------------------------------------------------------\u001b[39m",
      "\u001b[31mStreamingQueryException\u001b[39m                   Traceback (most recent call last)",
      "\u001b[36mCell\u001b[39m\u001b[36m \u001b[39m\u001b[32mIn[1]\u001b[39m\u001b[32m, line 63\u001b[39m\n\u001b[32m     61\u001b[39m \u001b[38;5;66;03m# 7️⃣ Await termination\u001b[39;00m\n\u001b[32m     62\u001b[39m \u001b[38;5;28mprint\u001b[39m(\u001b[33m\"\u001b[39m\u001b[33mStarting streams... waiting for termination.\u001b[39m\u001b[33m\"\u001b[39m)\n\u001b[32m---> \u001b[39m\u001b[32m63\u001b[39m \u001b[43mspark\u001b[49m\u001b[43m.\u001b[49m\u001b[43mstreams\u001b[49m\u001b[43m.\u001b[49m\u001b[43mawaitAnyTermination\u001b[49m\u001b[43m(\u001b[49m\u001b[43m)\u001b[49m\n",
      "\u001b[36mFile \u001b[39m\u001b[32m~\\IdeaProjects\\parcel-tracking-iot-based\\spark-streaming-pipeline\\venv\\Lib\\site-packages\\pyspark\\sql\\streaming\\query.py:596\u001b[39m, in \u001b[36mStreamingQueryManager.awaitAnyTermination\u001b[39m\u001b[34m(self, timeout)\u001b[39m\n\u001b[32m    594\u001b[39m     \u001b[38;5;28;01mreturn\u001b[39;00m \u001b[38;5;28mself\u001b[39m._jsqm.awaitAnyTermination(\u001b[38;5;28mint\u001b[39m(timeout * \u001b[32m1000\u001b[39m))\n\u001b[32m    595\u001b[39m \u001b[38;5;28;01melse\u001b[39;00m:\n\u001b[32m--> \u001b[39m\u001b[32m596\u001b[39m     \u001b[38;5;28;01mreturn\u001b[39;00m \u001b[38;5;28;43mself\u001b[39;49m\u001b[43m.\u001b[49m\u001b[43m_jsqm\u001b[49m\u001b[43m.\u001b[49m\u001b[43mawaitAnyTermination\u001b[49m\u001b[43m(\u001b[49m\u001b[43m)\u001b[49m\n",
      "\u001b[36mFile \u001b[39m\u001b[32m~\\IdeaProjects\\parcel-tracking-iot-based\\spark-streaming-pipeline\\venv\\Lib\\site-packages\\py4j\\java_gateway.py:1322\u001b[39m, in \u001b[36mJavaMember.__call__\u001b[39m\u001b[34m(self, *args)\u001b[39m\n\u001b[32m   1316\u001b[39m command = proto.CALL_COMMAND_NAME +\\\n\u001b[32m   1317\u001b[39m     \u001b[38;5;28mself\u001b[39m.command_header +\\\n\u001b[32m   1318\u001b[39m     args_command +\\\n\u001b[32m   1319\u001b[39m     proto.END_COMMAND_PART\n\u001b[32m   1321\u001b[39m answer = \u001b[38;5;28mself\u001b[39m.gateway_client.send_command(command)\n\u001b[32m-> \u001b[39m\u001b[32m1322\u001b[39m return_value = \u001b[43mget_return_value\u001b[49m\u001b[43m(\u001b[49m\n\u001b[32m   1323\u001b[39m \u001b[43m    \u001b[49m\u001b[43manswer\u001b[49m\u001b[43m,\u001b[49m\u001b[43m \u001b[49m\u001b[38;5;28;43mself\u001b[39;49m\u001b[43m.\u001b[49m\u001b[43mgateway_client\u001b[49m\u001b[43m,\u001b[49m\u001b[43m \u001b[49m\u001b[38;5;28;43mself\u001b[39;49m\u001b[43m.\u001b[49m\u001b[43mtarget_id\u001b[49m\u001b[43m,\u001b[49m\u001b[43m \u001b[49m\u001b[38;5;28;43mself\u001b[39;49m\u001b[43m.\u001b[49m\u001b[43mname\u001b[49m\u001b[43m)\u001b[49m\n\u001b[32m   1325\u001b[39m \u001b[38;5;28;01mfor\u001b[39;00m temp_arg \u001b[38;5;129;01min\u001b[39;00m temp_args:\n\u001b[32m   1326\u001b[39m     \u001b[38;5;28;01mif\u001b[39;00m \u001b[38;5;28mhasattr\u001b[39m(temp_arg, \u001b[33m\"\u001b[39m\u001b[33m_detach\u001b[39m\u001b[33m\"\u001b[39m):\n",
      "\u001b[36mFile \u001b[39m\u001b[32m~\\IdeaProjects\\parcel-tracking-iot-based\\spark-streaming-pipeline\\venv\\Lib\\site-packages\\pyspark\\errors\\exceptions\\captured.py:185\u001b[39m, in \u001b[36mcapture_sql_exception.<locals>.deco\u001b[39m\u001b[34m(*a, **kw)\u001b[39m\n\u001b[32m    181\u001b[39m converted = convert_exception(e.java_exception)\n\u001b[32m    182\u001b[39m \u001b[38;5;28;01mif\u001b[39;00m \u001b[38;5;129;01mnot\u001b[39;00m \u001b[38;5;28misinstance\u001b[39m(converted, UnknownException):\n\u001b[32m    183\u001b[39m     \u001b[38;5;66;03m# Hide where the exception came from that shows a non-Pythonic\u001b[39;00m\n\u001b[32m    184\u001b[39m     \u001b[38;5;66;03m# JVM exception message.\u001b[39;00m\n\u001b[32m--> \u001b[39m\u001b[32m185\u001b[39m     \u001b[38;5;28;01mraise\u001b[39;00m converted \u001b[38;5;28;01mfrom\u001b[39;00m\u001b[38;5;250m \u001b[39m\u001b[38;5;28;01mNone\u001b[39;00m\n\u001b[32m    186\u001b[39m \u001b[38;5;28;01melse\u001b[39;00m:\n\u001b[32m    187\u001b[39m     \u001b[38;5;28;01mraise\u001b[39;00m\n",
      "\u001b[31mStreamingQueryException\u001b[39m: [STREAM_FAILED] Query [id = 90f5c898-d3c9-415d-b3c9-f85dcbdd113c, runId = a6d892f6-886a-4a69-8da7-f57cf379e7a0] terminated with exception: 'boolean org.apache.hadoop.io.nativeio.NativeIO$Windows.access0(java.lang.String, int)'"
     ]
    }
   ],
   "source": [
    "from pyspark.sql import SparkSession\n",
    "from pyspark.sql.functions import col, from_json\n",
    "from pyspark.sql.types import StructType, StringType, DoubleType\n",
    "import os\n",
    "\n",
    "# 1️⃣ Create SparkSession\n",
    "# No HADOOP_HOME, no winutils, no warehouse config needed. It just works.\n",
    "spark = SparkSession.builder \\\n",
    "    .appName(\"ParcelTrackingStreaming\") \\\n",
    "    .config(\"spark.driver.memory\", \"4g\") \\\n",
    "    .config(\"spark.jars.packages\", \"org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.7\") \\\n",
    "    .getOrCreate()\n",
    "\n",
    "spark.sparkContext.setLogLevel(\"ERROR\")\n",
    "print(\"Spark version:\", spark.version)\n",
    "\n",
    "# 2️⃣ Define schema (same as before)\n",
    "gps_schema = StructType().add(\"package_id\", StringType()).add(\"latitude\", DoubleType()).add(\"longitude\", DoubleType())\n",
    "temperature_schema = StructType().add(\"package_id\", StringType()).add(\"temperature\", DoubleType())\n",
    "battery_schema = StructType().add(\"package_id\", StringType()).add(\"battery_level\", DoubleType())\n",
    "\n",
    "# 3️⃣ Read from Kafka - USE THE SPECIAL DOCKER ADDRESS\n",
    "df = spark.readStream \\\n",
    "    .format(\"kafka\") \\\n",
    "    .option(\"kafka.bootstrap.servers\", \"host.docker.internal:9092\") \\\n",
    "    .option(\"subscribe\", \"gps,temperature,battery\") \\\n",
    "    .option(\"startingOffsets\", \"latest\") \\\n",
    "    .load()\n",
    "\n",
    "# 4️⃣ Convert value (same as before)\n",
    "df = df.selectExpr(\"topic\", \"CAST(value AS STRING) as value\")\n",
    "\n",
    "# 5️⃣ Parse messages (same as before)\n",
    "gps_df = df.filter(col(\"topic\") == \"gps\").select(from_json(col(\"value\"), gps_schema).alias(\"data\")).select(\"data.*\")\n",
    "temperature_df = df.filter(col(\"topic\") == \"temperature\").select(from_json(col(\"value\"), temperature_schema).alias(\"data\")).select(\"data.*\")\n",
    "battery_df = df.filter(col(\"topic\") == \"battery\").select(from_json(col(\"value\"), battery_schema).alias(\"data\")).select(\"data.*\")\n",
    "\n",
    "# 6️⃣ Write stream\n",
    "# We can use simple relative paths for checkpoints. They are created inside the container's file system.\n",
    "gps_query = gps_df.writeStream \\\n",
    "    .outputMode(\"append\") \\\n",
    "    .format(\"console\") \\\n",
    "    .option(\"truncate\", False) \\\n",
    "    .option(\"checkpointLocation\", \"/tmp/spark-checkpoints/gps\") \\\n",
    "    .start()\n",
    "\n",
    "temperature_query = temperature_df.writeStream \\\n",
    "    .outputMode(\"append\") \\\n",
    "    .format(\"console\") \\\n",
    "    .option(\"truncate\", False) \\\n",
    "    .option(\"checkpointLocation\", \"/tmp/spark-checkpoints/temperature\") \\\n",
    "    .start()\n",
    "\n",
    "battery_query = battery_df.writeStream \\\n",
    "    .outputMode(\"append\") \\\n",
    "    .format(\"console\") \\\n",
    "    .option(\"truncate\", False) \\\n",
    "    .option(\"checkpointLocation\", \"/tmp/spark-checkpoints/battery\") \\\n",
    "    .start()\n",
    "\n",
    "# 7️⃣ Await termination\n",
    "print(\"Starting streams... waiting for termination.\")\n",
    "spark.streams.awaitAnyTermination()"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": null,
   "id": "e4aa2a9b-bd26-4848-a622-ebe8810eb45a",
   "metadata": {},
   "outputs": [],
   "source": []
  }
 ],
 "metadata": {
  "kernelspec": {
   "display_name": "Python (venv)",
   "language": "python",
   "name": "venv"
  },
  "language_info": {
   "codemirror_mode": {
    "name": "ipython",
    "version": 3
   },
   "file_extension": ".py",
   "mimetype": "text/x-python",
   "name": "python",
   "nbconvert_exporter": "python",
   "pygments_lexer": "ipython3",
   "version": "3.11.9"
  }
 },
 "nbformat": 4,
 "nbformat_minor": 5
}
