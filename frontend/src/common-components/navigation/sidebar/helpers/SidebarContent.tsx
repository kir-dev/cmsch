import { Stack } from '@chakra-ui/react'
import { useConfigContext } from '../../../../api/contexts/config/ConfigContext'
import { NavItemNoChildren } from '../NavItemNoChildren'
import { NavItemWithChildren } from '../NavItemWithChildren'

type Props = {
  onClose: () => void
}

export const SidebarContent = ({ onClose }: Props) => {
  const config = useConfigContext()

  return (
    <Stack pt={2}>
      {config?.menu.map((menu) =>
        menu.children && menu.children.length > 0 ? (
          <NavItemWithChildren key={menu.name} menu={menu} onButtonClickSideEffect={onClose} />
        ) : (
          <NavItemNoChildren key={menu.name} menu={menu} onButtonClickSideEffect={onClose} />
        )
      )}
    </Stack>
  )
}
