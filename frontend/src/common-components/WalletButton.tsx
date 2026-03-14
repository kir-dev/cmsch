import AppleWalletImage from '@/assets/AppleWalletHu.svg'
import GoogleWalletImage from '@/assets/GoogleWalletHu.svg'
import { PASS_SERVER_URL, PASS_TEMPLATE } from '@/util/configs/environment.config'
import { joinPath } from '@/util/core-functions.util'
import { useMemo } from 'react'

interface WalletButtonProps {
  userId: string
  name: string
  type: 'google' | 'apple'
}

export function WalletButton({ userId, type, name }: WalletButtonProps) {
  const url = useMemo(() => {
    if (!PASS_SERVER_URL || !PASS_TEMPLATE) return
    const urlFactory = new URL(joinPath(PASS_SERVER_URL, type, PASS_TEMPLATE))
    urlFactory.searchParams.append('name', name)
    urlFactory.searchParams.append('userId', userId)
    return urlFactory.toString()
  }, [name, userId, type])

  if (!url) return null

  return (
    <img
      className="cursor-pointer max-w-full w-48 mb-3"
      src={type === 'apple' ? AppleWalletImage : GoogleWalletImage}
      alt={type === 'apple' ? 'Apple Wallet' : 'Google Wallet'}
      onClick={() => {
        window.open(url)
      }}
    />
  )
}
