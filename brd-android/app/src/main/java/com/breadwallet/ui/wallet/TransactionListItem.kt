package com.breadwallet.ui.wallet

import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.format.DateUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.breadwallet.R
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.databinding.ItemSwapDetailsBinding
import com.breadwallet.databinding.TxItemBinding
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.util.BRDateUtil
import com.breadwallet.tools.util.Utils
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.isBitcoinLike
import com.fabriik.trade.data.response.ExchangeOrderStatus.COMPLETE
import com.fabriik.trade.data.response.ExchangeOrderStatus.PENDING
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import java.util.*

private const val DP_120 = 120
private const val PROGRESS_FULL = 100

class TransactionListItem(
    transaction: WalletTransaction,
    var isCryptoPreferred: Boolean
) : ModelAbstractItem<WalletTransaction, TransactionListItem.ViewHolder>(transaction) {

    override val layoutRes: Int =
        if (transaction.exchangeData != null) R.layout.item_swap_details else
            R.layout.tx_item

    override val type: Int = R.id.transaction_item

    override var identifier: Long = model.txHash.hashCode().toLong()

    override fun getViewHolder(v: View) = ViewHolder(v)

    private val isSwap = transaction.exchangeData != null

    inner class ViewHolder(
        v: View
    ) : FastAdapter.ViewHolder<TransactionListItem>(v) {

        override fun bindView(item: TransactionListItem, payloads: List<Any>) {
            if (isSwap) {
                val binding = ItemSwapDetailsBinding.bind(itemView)
                setSwapContent(binding, item.model)
            } else {
                val binding = TxItemBinding.bind(itemView)
                setTransferContent(binding, item.model, item.isCryptoPreferred)
            }
        }

        override fun unbindView(item: TransactionListItem) {
            //empty
        }

        private fun setSwapContent(
            binding: ItemSwapDetailsBinding,
            transaction: WalletTransaction
        ) {
            val status = transaction.exchangeData?.status!!
            val context = itemView.context
            val transactionTitle =
                if (status == PENDING) {
                    context.getString(
                        R.string.Swap_TransactionItem_Pending,
                        transaction.currencyCode.toUpperCase(Locale.ROOT)
                    )
                } else {
                    context.getString(
                        R.string.Swap_TransactionItem_Swapped,
                        transaction.currencyCode.toUpperCase(Locale.ROOT)
                    )
                }

            with(binding) {
                tvTransactionTitle.text = transactionTitle
                tvTransactionDate.text = BRDateUtil.getShortDate(transaction.timeStamp)
                tvTransactionValue.text = transaction.amount.toString()
                tvTransactionValueDollars.text =
                    transaction.amountInFiat.formatFiatForUi(BRSharedPrefs.getPreferredFiatIso())
            }

            when (status) {
                COMPLETE -> {
                    binding.icItemBg.imageTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                com.fabriik.trade.R.color.swap_light_green
                            )
                        )

                    binding.icItem.imageTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                com.fabriik.trade.R.color.swap_green
                            )
                        )

                    binding.tvTransactionValue.setTextColor(context.getColor(R.color.swap_green))
                }

                else -> Unit
            }
        }

        @Suppress("LongMethod", "ComplexMethod")
        private fun setTransferContent(
            binding: TxItemBinding,
            transaction: WalletTransaction,
            isCryptoPreferred: Boolean
        ) {
            val context = itemView.context
            val commentString = transaction.memo

            val received = transaction.isReceived

            binding.imageTransferDirection.setBackgroundResource(
                when {
                    transaction.progress < PROGRESS_FULL ->
                        R.drawable.transfer_in_progress
                    transaction.isErrored -> R.drawable.transfer_failed
                    transaction.gift != null -> when {
                        transaction.gift.claimed -> R.drawable.transfer_gift_claimed
                        transaction.gift.reclaimed -> R.drawable.transfer_gift_reclaimed
                        else -> R.drawable.transfer_gift_unclaimed
                    }
                    received -> R.drawable.transfer_receive
                    else -> R.drawable.transfer_send
                }
            )

            binding.txAmount.setTextColor(
                context.getColor(
                    when {
                        received -> R.color.transaction_amount_received_color
                        else -> R.color.total_assets_usd_color
                    }
                )
            )

            val preferredCurrencyCode = when {
                isCryptoPreferred -> transaction.currencyCode
                else -> BRSharedPrefs.getPreferredFiatIso()
            }
            val amount = when {
                isCryptoPreferred -> transaction.amount
                else -> transaction.amountInFiat
            }.run { if (received) this else negate() }

            val formattedAmount = when {
                isCryptoPreferred -> amount.formatCryptoForUi(preferredCurrencyCode)
                else -> amount.formatFiatForUi(preferredCurrencyCode)
            }

            binding.txAmount.text = formattedAmount

           when {
                !transaction.gift?.recipientName.isNullOrBlank() -> {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_toRecipient, "")
                    binding.txDescriptionValue.text = transaction.gift?.recipientName
                }
                transaction.isStaking -> {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_stakingTo, "")
                    binding.txDescriptionValue.text = transaction.toAddress
                }
                commentString == null -> {
                    binding.txDescriptionLabel.text = ""
                    binding.txDescriptionValue.text = ""
                }
                commentString.isNotEmpty() -> {
                    binding.txDescriptionLabel.text = ""
                    binding.txDescriptionValue.text = commentString
                }
                transaction.isFeeForToken -> {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_tokenTransfer, transaction.feeToken)
                    binding.txDescriptionValue.text = commentString
                }
                received -> {
                    if (transaction.isComplete) {
                        if (transaction.currencyCode.isBitcoinLike()) {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivedVia, "")
                            binding.txDescriptionValue.text = transaction.toAddress
                        } else {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivedFrom, "")
                            binding.txDescriptionValue.text = transaction.fromAddress
                        }
                    } else {
                        if (transaction.currencyCode.isBitcoinLike()) {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivingVia, "")
                            binding.txDescriptionValue.text = transaction.toAddress
                        } else {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivingFrom, "")
                            binding.txDescriptionValue.text = transaction.fromAddress
                        }
                    }
                }
                else -> if (transaction.isComplete) {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_sentTo, "")
                    binding.txDescriptionValue.text = transaction.toAddress
                } else {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_sendingTo, "")
                    binding.txDescriptionValue.text = transaction.toAddress
                }
            }

            val timeStamp = transaction.timeStamp
            binding.txTitle.text = when {
                timeStamp == 0L || transaction.isPending -> buildString {
                    append(transaction.confirmations)
                    append('/')
                    append(transaction.confirmationsUntilFinal)
                    append(' ')
                    append(context.getString(R.string.TransactionDetails_confirmationsLabel))
                }
                DateUtils.isToday(timeStamp) -> BRDateUtil.getTime(timeStamp)
                else -> BRDateUtil.getShortDate(timeStamp)
            }

            // If this transaction failed, show the "FAILED" indicator in the cell
            if (transaction.isErrored) {
                showTransactionFailed()
            } else {
                showTransactionProgress(transaction.progress)
            }
        }

        private fun showTransactionProgress(progress: Int) {
            val context = itemView.context
            val binding = TxItemBinding.bind(itemView)
            with(binding) {
                txTitle.isVisible = true
                txAmount.isVisible = true
                imageTransferDirection.isVisible = true

                val textColor = context.getColor(R.color.total_assets_usd_color)
                txTitle.setTextColor(textColor)
                txAmount.paintFlags = 0 // clear strike-through

                if (progress < PROGRESS_FULL) {
                    if (imageTransferDirection.progressDrawable == null) {
                        imageTransferDirection.progressDrawable =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.transfer_progress_drawable
                            )
                    }
                    imageTransferDirection.setProgress(progress, true)
                    txDescriptionValue.maxWidth = Utils.getPixelsFromDps(context, DP_120)
                } else {
                    imageTransferDirection.progressDrawable = null
                    imageTransferDirection.progress = 0
                }
            }
        }

        private fun showTransactionFailed() {
            val context = itemView.context
            val binding = TxItemBinding.bind(itemView)
            with(binding) {
                imageTransferDirection.progressDrawable = null

                val errorColor = context.getColor(R.color.ui_error)
                txTitle.setText(R.string.Transaction_failed)
                txTitle.setTextColor(errorColor)
                txAmount.setTextColor(errorColor)
                txAmount.paintFlags = txAmount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }
}
