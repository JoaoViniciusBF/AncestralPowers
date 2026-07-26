# Como Modificar a Textura do Inventário

## Textura Original Extraída

A textura vanilla do inventário está em: `vanilla_inventory_original.png` (256x256 pixels)

## Onde Colocar a Textura Modificada

Para que o mod use uma textura customizada, você precisa criar/substituir o arquivo:

```
src/main/resources/assets/minecraft/textures/gui/container/inventory.png
```

**IMPORTANTE:** Note que é `assets/minecraft/` (não `ancestralpowers`), pois estamos substituindo a textura vanilla.

## Passos para Modificar

### 1. Abrir a Textura Original
```bash
# A textura está no arquivo: vanilla_inventory_original.png
```

### 2. Editar com um Editor de Imagens
Use GIMP, Photoshop, Paint.NET, Krita, ou qualquer editor que suporte PNG com transparência.

### 3. Layout da Textura (256x256)

#### Coordenadas dos elementos na textura:
- **Background principal**: 0,0 até 176,166
- **Slots de armadura (4 ícones)**: 
  - Capacete: 7,8 (16x16)
  - Peitoral: 7,26 (16x16)
  - Calças: 7,44 (16x16)
  - Botas: 7,62 (16x16)
- **Offhand icon**: 77,62 (16x16)
- **Crafting grid**: 98,18 até 133,53
- **Player render area**: ~26,8 até 75,75 (área onde o player é renderizado)

### 4. Modificações Sugeridas

Como você moveu as armaduras para a direita, você pode:

1. **Expandir o background** horizontalmente para cobrir até onde os slots estão
2. **Mover/remover os ícones de armadura** da posição original
3. **Adicionar decorações** na área direita onde as armaduras agora ficam
4. **Limpar a área central** onde o player costumava ficar

### 5. Salvar e Testar

Depois de modificar:

1. Salve como PNG com transparência
2. Copie para: `src/main/resources/assets/minecraft/textures/gui/container/inventory.png`
3. Compile: `./gradlew build`
4. Teste: `./gradlew runClient`

## Exemplo de Modificação Simples

Para começar rápido, você pode:

1. Abrir `vanilla_inventory_original.png`
2. Expandir a tela para 256x256 (se necessário)
3. Estender o background cinza para a direita até x=220
4. Adicionar slots visuais nas posições onde você colocou as armaduras
5. Salvar e testar

## Alternativa: Criar do Zero

Se preferir criar do zero, crie uma imagem 256x256 com:
- Fundo transparente
- Background do inventário (cor cinza: #C6C6C6)
- Slots escuros para cada posição de item (cor: #8B8B8B)
- Bordas e decorações ao seu gosto

## Posições Atuais dos Slots (configuráveis via comando)

Use `/inventorypos show` no jogo para ver onde os slots estão posicionados.
Ajuste a textura para combinar com essas posições!

## Resource Pack (Alternativa)

Se não quiser modificar o mod, você pode criar um Resource Pack:
1. Crie uma pasta com a estrutura: `assets/minecraft/textures/gui/container/`
2. Coloque sua `inventory.png` modificada lá
3. Adicione um `pack.mcmeta`
4. Use como resource pack no Minecraft
