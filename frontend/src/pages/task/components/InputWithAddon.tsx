import { InputGroup, InputRightAddon } from '@chakra-ui/react'
import { PropsWithChildren } from 'react'

interface InputWithAddonProps extends PropsWithChildren {
  suffix: string | undefined
}

export const InputWithAddon = ({ suffix, children }: InputWithAddonProps) => {
  if (suffix) {
    return (
      <InputGroup>
        {children}
        <InputRightAddon children={suffix} />
      </InputGroup>
    )
  }
  return <>{children}</>
}
