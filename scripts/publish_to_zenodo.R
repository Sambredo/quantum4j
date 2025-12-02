#!/usr/bin/env Rscript
# Publish docs/quantum4j-paper.Rmd as a Word (.docx) to Zenodo using the REST API
# Usage: set environment variable ZENODO_TOKEN (your Zenodo access token) prior to running.

library(rmarkdown)
library(httr)
library(jsonlite)

# Config
rmd_file <- "docs/quantum4j-paper.Rmd"
output_docx <- "docs/quantum4j-paper.docx"
zenodo_api <- "https://zenodo.org/api/deposit/depositions"
# Use Zenodo sandbox for testing: https://sandbox.zenodo.org/api/deposit/depositions
# If using sandbox, set zenodo_api accordingly.

# 1) Render Word doc
message("Rendering RMarkdown to Word (.docx)...")
rmarkdown::render(rmd_file, output_format = "word_document", output_file = output_docx)
stopifnot(file.exists(output_docx))
message("Rendered: ", output_docx)

# 2) Publish to Zenodo
token <- Sys.getenv("ZENODO_TOKEN")
if (identical(token, "") || is.na(token)) {
  stop("ZENODO_TOKEN environment variable not set. Export your Zenodo access token before running.")
}

# Create a new (empty) deposition
message("Creating new deposition on Zenodo...")
created <- POST(
  url = paste0(zenodo_api, "?access_token=", token),
  add_headers(`Content-Type` = "application/json"),
  body = toJSON(list(metadata = list(title = "Quantum4J: A JVM‑Native Framework for Quantum Circuit Simulation, Transpilation, and Hybrid Integration",
                                      upload_type = "publication",
                                      publication_type = "other",
                                      description = "Implementation-centered paper for the Quantum4J Java library (analysis produced from the repository).",
                                      creators = list(list(name = "vijayanandg"))
                                      ))),
  encode = "json"
)
stop_for_status(created)
created_content <- content(created, as = "parsed", simplifyVector = TRUE)
dep_id <- created_content$id
message("Created deposition id: ", dep_id)

# 3) Upload the Word file
message("Uploading file to deposition...")
upload_url <- sprintf("%s/%s/files?access_token=%s", zenodo_api, dep_id, token)
resp <- POST(upload_url, body = list(file = upload_file(output_docx)))
stop_for_status(resp)
message("Uploaded file: ", output_docx)

# 4) Optionally update metadata (add DOI related fields, license, keywords, etc.)
update_payload <- list(
  metadata = list(
    title = "Quantum4J: A JVM‑Native Framework for Quantum Circuit Simulation, Transpilation, and Hybrid Integration",
    upload_type = "publication",
    publication_type = "report",
    description = list(description_type = "publication-summary",
                       description = "Implementation and analysis report for the Quantum4J Java library. The document is an analysis derived from the repository at c:/Users/vijay/OneDrive/Desktop/Projects/quantum4j."),
    creators = list(list(name = "vijayanandg")),
    keywords = c("Quantum", "Java", "Simulation", "Transpiler", "OpenQASM"),
    license = "CC-BY-4.0"
  )
)

message("Updating metadata on the deposition...")
update_resp <- PUT(paste0(zenodo_api, "/", dep_id, "?access_token=", token),
                   body = toJSON(update_payload, auto_unbox = TRUE),
                   add_headers(`Content-Type` = "application/json"))
stop_for_status(update_resp)
message("Metadata updated.")

# 5) Publish deposition
message("Publishing deposition (this will create a DOI / make entry public)...")
publish_resp <- POST(paste0(zenodo_api, "/", dep_id, "/actions/publish?access_token=", token))
stop_for_status(publish_resp)
publish_content <- content(publish_resp, as = "parsed", simplifyVector = TRUE)
message("Published! DOI: ", publish_content$doi)

# Basic success output
cat("Zenodo deposition published. DOI: ", publish_content$doi, "\n")
