# debug_models_proper.py
from pyspark.sql import SparkSession
from pyspark.ml import PipelineModel

print(" Debugging saved models...")
spark = SparkSession.builder \
    .appName("DebugModels") \
    .config("spark.sql.adaptive.enabled", "false") \
    .getOrCreate()

def debug_model(model_path, model_name):
    print(f"\n{'='*50}")
    print(f" Debugging {model_name}")
    print(f"{'='*50}")

    try:
        # Load the model
        model = PipelineModel.load(model_path)
        print(f"{model_name} loaded successfully")

        # Inspect each stage
        for i, stage in enumerate(model.stages):
            print(f"\nStage {i}: {type(stage).__name__}")

            # Get input columns
            if hasattr(stage, 'getInputCols'):
                input_cols = stage.getInputCols()
                print(f"  Input Columns: {input_cols}")
            elif hasattr(stage, 'getInputCol'):
                input_col = stage.getInputCol()
                print(f"  Input Column: {input_col}")

            # Get output columns
            if hasattr(stage, 'getOutputCol'):
                output_col = stage.getOutputCol()
                print(f"  Output Column: {output_col}")

            # For StringIndexer, show what it's indexing
            if hasattr(stage, 'getLabels'):
                try:
                    labels = stage.getLabels()
                    print(f"  Labels (first 5): {labels[:5] if labels else 'None'}")
                except:
                    pass

    except Exception as e:
        print(f"Error loading {model_name}: {e}")

# Debug all models
debug_model("/app/models/kmeans_pipeline", "KMeans Model")
debug_model("/app/models/rf_pipeline", "Random Forest Model")
debug_model("/app/models/iforest_pipeline", "Isolation Forest Model")

spark.stop()