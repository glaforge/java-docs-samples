/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aiplatform;

// [START aiplatform_sdk_tuning]
// [START generativeaionvertexai_sdk_tuning]
import com.google.cloud.aiplatform.v1.CreatePipelineJobRequest;
import com.google.cloud.aiplatform.v1.LocationName;
import com.google.cloud.aiplatform.v1.PipelineJob;
import com.google.cloud.aiplatform.v1.PipelineJob.RuntimeConfig;
import com.google.cloud.aiplatform.v1.PipelineServiceClient;
import com.google.cloud.aiplatform.v1.PipelineServiceSettings;
import com.google.protobuf.Value;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreatePipelineJobModelTuningSample {

  public static void main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    String project = "PROJECT";
    String location = "europe-west4"; // europe-west4 and us-central1 are the supported regions
    String pipelineJobDisplayName = "PIPELINE_JOB_DISPLAY_NAME";
    String modelDisplayName = "MODEL_DISPLAY_NAME";
    String outputDir = "OUTPUT_DIR";
    String datasetUri = "DATASET_URI";
    int trainingSteps = 300;

    createPipelineJobModelTuningSample(
        project,
        location,
        pipelineJobDisplayName,
        modelDisplayName,
        outputDir,
        datasetUri,
        trainingSteps);
  }

  // Create a model tuning job
  public static void createPipelineJobModelTuningSample(
      String project,
      String location,
      String pipelineJobDisplayName,
      String modelDisplayName,
      String outputDir,
      String datasetUri,
      int trainingSteps)
      throws IOException {
    final String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
    PipelineServiceSettings pipelineServiceSettings =
        PipelineServiceSettings.newBuilder().setEndpoint(endpoint).build();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests.
    try (PipelineServiceClient client = PipelineServiceClient.create(pipelineServiceSettings)) {
      Map<String, Value> parameterValues = new HashMap<>();
      parameterValues.put("project", stringToValue(project));
      parameterValues.put("model_display_name", stringToValue(modelDisplayName));
      parameterValues.put("dataset_uri", stringToValue(datasetUri));
      parameterValues.put(
          "location",
          stringToValue(
              "us-central1")); // Deployment is only supported in us-central1 for Public Preview
      parameterValues.put("large_model_reference", stringToValue("text-bison@001"));
      parameterValues.put("train_steps", numberToValue(trainingSteps));
      parameterValues.put("accelerator_type", stringToValue("GPU")); // Optional: GPU or TPU

      RuntimeConfig runtimeConfig =
          RuntimeConfig.newBuilder()
              .setGcsOutputDirectory(outputDir)
              .putAllParameterValues(parameterValues)
              .build();

      PipelineJob pipelineJob =
          PipelineJob.newBuilder()
              .setTemplateUri(
                  "https://us-kfp.pkg.dev/ml-pipeline/large-language-model-pipelines/tune-large-model/v2.0.0")
              .setDisplayName(pipelineJobDisplayName)
              .setRuntimeConfig(runtimeConfig)
              .build();

      LocationName parent = LocationName.of(project, location);
      CreatePipelineJobRequest request =
          CreatePipelineJobRequest.newBuilder()
              .setParent(parent.toString())
              .setPipelineJob(pipelineJob)
              .build();

      PipelineJob response = client.createPipelineJob(request);
      System.out.format("response: %s\n", response);
      System.out.format("Name: %s\n", response.getName());
    }
  }

  static Value stringToValue(String str) {
    return Value.newBuilder().setStringValue(str).build();
  }

  static Value numberToValue(int n) {
    return Value.newBuilder().setNumberValue(n).build();
  }
}

// [END aiplatform_sdk_tuning]
// [END generativeaionvertexai_sdk_tuning]
