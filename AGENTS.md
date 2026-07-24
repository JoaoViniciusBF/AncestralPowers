# Instruções para Agentes

Antes de trabalhar em qualquer pedido neste repositório, leia este arquivo cuidadosamente.

## Finalização de Tarefas (OBRIGATÓRIO)

Sempre que concluir o conjunto de tarefas ou o pedido da sessão atual:

1. Informe claramente que o trabalho foi **Finalizado**.
2. Inclua um **resumo bem enxuto** em tópicos do que foi feito.
3. Mencione se a **compilação (build) e/ou teste** foi executada e o seu resultado.
4. Caso não tenha sido possível buildar ou testar (por falta de pacotes/ambientação), informe o motivo brevemente.

Formato sugerido:

```text
Finalizado.
- Ajustado ClasseX para resolver o erro Y.
- Criado ItemZ.
- Build: sucesso.
```

## Regras de Código e Commits

1. **Evite substituir ou sobrescrever arquivos inteiros se puder editar por partes.** Modifique apenas o necessário.
2. **Versionamento Direto:** Quando for fechar um grande pacote de novas funcionalidades ou Correção de bugs vitais, pergunte ou proponha Bump de versão (Semantic Versioning) em `gradle.properties`.
3. **Traduções:** Ao adicionar novos items/blocks/features visíveis ingame, DEVE ser incluída a key de tradução correspondente nos arquivos `en_us.json` e `pt_br.json` (dentro de `src/main/resources/assets/*/lang/`).
4. **Tooltips Descritivas e Bonitas:** Sempre que for criar itens mágicos ou customizados (RPG), forneça descrições ricas adicionando métodos de `appendTooltip` com cores (`Formatting`).
