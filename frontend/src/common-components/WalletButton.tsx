import { Image } from '@chakra-ui/react'
import { useMemo } from 'react'
import AppleWalletImage from '../assets/AppleWalletHu.svg'
import GoogleWalletImage from '../assets/GoogleWalletHu.svg'
import { PASS_SERVER_URL, PASS_TEMPLATE } from '../util/configs/environment.config'
import { joinPath } from '../util/core-functions.util'

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
  }, [PASS_SERVER_URL, PASS_TEMPLATE, name, userId, type])

  if (!url) return null

  return (
    <Image
      cursor="pointer"
      maxW="100%"
      w={48}
      mb={3}
      src={type === 'apple' ? AppleWalletImage : GoogleWalletImage}
      onClick={() => {
        window.open(url)
      }}
    />
  )
}
