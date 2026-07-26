# Modificações no Layout do Inventário

## Implementação

Foram criados mixins para modificar a tela de inventário padrão do Minecraft, movendo o player e os slots de armadura para a direita.

## Arquivos Criados/Modificados

### Mixins
- `src/main/java/dev/joaq/ancestralpowers/mixin/client/InventoryScreenMixin.java`
  - Injeta código no construtor do `InventoryScreen` para mover slots de armadura
  - Modifica a posição de renderização do player model

- `src/main/java/dev/joaq/ancestralpowers/mixin/client/SlotAccessor.java`
  - Interface accessor para modificar campos `x` e `y` dos slots (que são final)

- `src/main/java/dev/joaq/ancestralpowers/mixin/client/HandledScreenAccessor.java`
  - Interface accessor para acessar o handler da screen

### Configuração
- `src/main/resources/ancestralpowers.mixins.json`
  - Registra os novos mixins client-side

## Mudanças Visuais

- **Slots de Armadura** (índices 36-39): movidos 60 pixels para direita
- **Slot Offhand** (índice 40): movido 60 pixels para direita  
- **Renderização do Player**: movida 60 pixels para direita

## Resultado

O inventário padrão agora tem um layout estilo RPG, com:
- Player e armaduras na lateral direita
- Inventário principal mais centralizado
- Mais espaço livre na área central

## Versão

Mod version: 1.2.0
Minecraft: 1.21.8
Fabric Loader: 0.17.2
