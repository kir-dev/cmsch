import {Box, Tooltip} from "@chakra-ui/react";
import {TeamLabelView} from "../../../util/views/team.view.ts";
import {useMutedColor} from "../../../util/useDesaturated.ts";
import {isValidHex} from "../../../util/color.utils.ts";

const TeamLabel = ({ label }: { label: TeamLabelView }) => {

  return label.desc ?
    <LabelWithTooltio label={label} />
    :
    <LabelNoTooltip label={label} />
};

const LabelNoTooltip = ({ label }: { label: TeamLabelView }) => {

  const [color, desatColor] = label.color && isValidHex(label.color) ?
    [label.color, useMutedColor(label.color, 0.5)] : ["brand.600", "brand.200"]

  return (
    <Box
      border="2px solid"
      bg={desatColor}
      borderColor={color}
      color={color}
      px={3}
      py={0.5}
      borderRadius="xl"
      fontWeight="bold"
      fontSize={12}
      letterSpacing={1.2}

    >
      {label.name}
    </Box>
  );
};

const LabelWithTooltio = ({ label }: { label: TeamLabelView }) => {

  const [color, desatColor] = label.color && isValidHex(label.color) ?
    [label.color, useMutedColor(label.color, 0.5)] : ["brand.600", "brand.200"]

  return (
    <Tooltip label={label.desc} aria-label={label.desc}>
      <Box
        border="2px solid"
        bg={desatColor}
        borderColor={color}
        color={color}
        px={3}
        py={0.5}
        borderRadius="xl"
        fontWeight="bold"
        fontSize={12}
        letterSpacing={1.2}

      >
        {label.name}
      </Box>
    </Tooltip>
  );
};


export default TeamLabel;
