FROM apache/airflow:2.10.2

USER root

RUN apt-get update && \
    apt-get install -y chromium chromium-driver && \
    rm -rf /var/lib/apt/lists/*

USER airflow

COPY --chown=airflow:airflow airflow_requirements.txt /opt/airflow/airflow_requirements.txt
RUN pip install --no-cache-dir -r /opt/airflow/airflow_requirements.txt
