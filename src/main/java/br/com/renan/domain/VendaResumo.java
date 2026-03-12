package br.com.renan.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Java 21 — Record:
 * Um record é uma classe imutável e compacta para transportar dados (DTO).
 * O compilador gera automaticamente:
 *   - construtor canônico (todos os campos)
 *   - getters (codigo(), cliente(), totalProdutos(), valorTotal(), dataVenda(), status())
 *   - equals(), hashCode() e toString()
 *
 * Use records para dados que NÃO precisam ser alterados após a criação,
 * como respostas de consulta, sumários, ou objetos de transferência de dados.
 *
 * IMPORTANTE: Records NÃO são adequados como entidades JPA (@Entity),
 * pois JPA exige construtores sem argumentos e campos mutáveis.
 * Use-os como DTOs intermediários ou retornos de consultas.
 *
 * Exemplo de criação a partir de uma Venda:
 *   var resumo = VendaResumo.fromVenda(venda);
 *   System.out.println(resumo.valorTotal());  // getter gerado automaticamente
 */
public record VendaResumo(
        String codigo,
        String nomeCliente,
        int totalProdutos,
        BigDecimal valorTotal,
        Instant dataVenda,
        Venda.Status status
) {
    public static VendaResumo fromVenda(Venda venda) {
        return new VendaResumo(
                venda.getCodigo(),
                venda.getCliente().getNome(),
                venda.getQuantidadeTotalProdutos(),
                venda.getValorTotal(),
                venda.getDataVenda(),
                venda.getStatus()
        );
    }
}
