# Textura do Inventário Customizado

## Localização
`src/main/resources/assets/ancestralpowers/textures/gui/container/custom_inventory.png`

## Especificações
- **Tamanho**: 256x256 pixels (formato padrão Minecraft)
- **Dimensões da GUI**: 195x166 pixels
- **Layout**: Inventário principal à esquerda, player e armaduras à direita

## Layout dos Slots

### Inventário Principal (esquerda)
- **Slots principais** (3 linhas x 9 colunas): Posição (7, 83) com espaçamento de 18px
- **Hotbar** (1 linha x 9 colunas): Posição (7, 141) com espaçamento de 18px

### Crafting (centro)
- **Grid 2x2**: Posição (97, 17) com espaçamento de 18px
- **Resultado**: Posição (153, 27)

### Armadura (direita)
- **4 slots de armadura**: Posição (164, 7) vertical com espaçamento de 18px
  - Slot 0: Capacete (164, 7)
  - Slot 1: Peitoral (164, 25)
  - Slot 2: Calças (164, 43)
  - Slot 3: Botas (164, 61)

### Offhand
- **Slot offhand**: Posição (76, 61)

## Como Criar a Textura

Você pode criar a textura usando qualquer editor de imagens (Photoshop, GIMP, Paint.NET, etc.):

1. Crie uma imagem 256x256 com fundo transparente
2. Desenhe um retângulo cinza (RGB: 139, 139, 139) de 195x166 para o fundo da GUI
3. Desenhe retângulos escuros (RGB: 55, 55, 55) de 16x16 para cada slot nas posições acima
4. Adicione detalhes decorativos ao gosto (bordas, sombras, etc.)
5. Salve como PNG com transparência

## Alternativa Rápida
Você pode extrair e modificar a textura vanilla do inventário:
- Localize `assets/minecraft/textures/gui/container/inventory.png` no jar do Minecraft
- Reorganize os slots conforme o layout acima
- Adicione espaço para o modelo do player renderizado (área em torno de x=26-75, y=8-75)
