# Fala-Texto

<div align="center">
  <img width="350" height="2000" alt="Image" src="https://github.com/user-attachments/assets/1d1e4d24-db10-4e1a-8598-04288cc1a91a" />
</div>

Para instalar as dependências do arquivo requirements.txt no Linux, utilize o terminal e o gerenciador de pacotes pip com o comando pip "install -r requirements.txt". Recomenda-se criar um ambiente virtual (venv) antes para evitar conflitos com pacotes globais do sistema.

## Passo a Passo 
Abra o terminal e navegue até a pasta do seu projeto:

por exemplo: 
cd /caminho/do/seu/projeto

Crie e ative um ambiente virtual (opcional, mas recomendado):

python3 -m venv venv #Comando para criar o ambiente virtual
source venv/bin/activate #Comando para ativar o ambiente Virtual

Instale as dependências:

pip install -r requirements.txt

Em seguida, clone o repositório do fine-tuning e preencha "login(token="*")" no local do asterisco insira o token criado no huggingface, após estes passos já é possível começar o treinamento. Obs:Este treinamento foi realizado focado em (GPU) não em (CPU)

Para avaliar seu modelo treinado utilize o arquivo "Avaliarmodelo.py" não á necessidade de instalar nenhuma nova depêndencia, os passos a serem tomados são, preencher a váriável (MODEL_PATH ="Caminho para a pasta aonde se encontrar seu modelo treinado localmente") e baixar arquivos de áudios alvos com base nos dados utilizado para o treinamento e encaminhar estes áudios na variável (AUDIO_FILE_PATH = "CAMINHO DO SEU ARQUIVO DE ÁUDIO").
