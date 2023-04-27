package com.laryisland.screenfx.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

// MidnightConfig v2.4.0

@SuppressWarnings("unchecked")
public abstract class MidnightConfig {
	private static final Pattern INTEGER_ONLY = Pattern.compile("(-?[0-9]*)");
	private static final Pattern DECIMAL_ONLY = Pattern.compile("-?([\\d]+\\.?[\\d]*|[\\d]*\\.?[\\d]+|\\.)");
	private static final Pattern HEXADECIMAL_ONLY = Pattern.compile("(-?[#0-9a-fA-F]*)");

	private static final List<EntryInfo> entries = new ArrayList<>();
	private static final Map<String, List<EntryInfo>> mapEntries = new LinkedHashMap<>();

	protected static class EntryInfo {
		Field field;
		Object widget;
		int width;
		boolean centered;
		Text error;
		Object defaultValue;
		Object value;
		String tempValue;
		boolean inLimits = true;
		String id;
		Text name;
		int index;
		ClickableWidget colorButton;
		Tab tab;
		String map;
		int mapPosition;
	}

	public static final Map<String,Class<?>> configClass = new HashMap<>();
	private static Path path;

	private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).excludeFieldsWithModifiers(Modifier.PRIVATE).addSerializationExclusionStrategy(new HiddenAnnotationExclusionStrategy()).setPrettyPrinting().create();

	@Environment(EnvType.CLIENT)
	public static class Tab {
		private final Text title;
		protected final GridWidget grid = new GridWidget();

		public Tab(Text title) { this.title = title; }

		public Text getTitle() { return this.title; }

		public void forEachChild(Consumer<ClickableWidget> consumer) {
			for (Element child : this.grid.children()) {
				consumer.accept((ClickableWidget) child);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TabManager {
		private final Consumer<ClickableWidget> tabLoadConsumer;
		private final Consumer<ClickableWidget> tabUnloadConsumer;
		@Nullable
		private Tab currentTab;

		public TabManager(Consumer<ClickableWidget> tabLoadConsumer, Consumer<ClickableWidget> tabUnloadConsumer) {
			this.tabLoadConsumer = tabLoadConsumer;
			this.tabUnloadConsumer = tabUnloadConsumer;
		}

		public void setCurrentTab(Tab tab, boolean clickSound) {
			if (!Objects.equals(this.currentTab, tab)) {
				if (this.currentTab != null) {
					this.currentTab.forEachChild(this.tabUnloadConsumer);
				}

				this.currentTab = tab;
				tab.forEachChild(this.tabLoadConsumer);

				if (clickSound) {
					MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				}
			}
		}

		@Nullable
		public Tab getCurrentTab() { return this.currentTab; }
	}

	@Environment(value=EnvType.CLIENT)
	public static class TabButtonWidget extends ClickableWidget {
		private static final Identifier TAB_TEXTURE = new Identifier("screenfx", "tab_button.png");
		private final TabManager tabManager;
		private final Tab tab;

		public TabButtonWidget(TabManager tabManager, Tab tab, int width, int height) {
			super(0, 0, width, height, tab.getTitle());
			this.tabManager = tabManager;
			this.tab = tab;
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShaderTexture(0, TAB_TEXTURE);
			TabButtonWidget.drawTexture(matrices, this.getX(), this.getY(), 0, this.getTextureV(), this.width, this.height, 94, 96);
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			int i = this.active ? -1 : -6250336;
			this.drawMessage(matrices, textRenderer, i);
			if (this.isCurrentTab()) {
				this.drawCurrentTabLine(matrices, textRenderer, i);
			}
		}

		public void drawMessage(MatrixStack matrices, TextRenderer textRenderer, int color) {
			int i = this.getX() + this.getWidth() / 2;
			int j = ((this.isCurrentTab() ? 0 : 3) + this.getHeight() - textRenderer.fontHeight) / 2 + this.getY() + 1;
			TabButtonWidget.drawCenteredTextWithShadow(matrices, textRenderer, this.getMessage().asOrderedText(), i, j, color);
		}

		private void drawCurrentTabLine(MatrixStack matrices, TextRenderer textRenderer, int color) {
			int i = Math.min(textRenderer.getWidth(this.getMessage()), this.getWidth() - 4);
			int j = this.getX() + (this.getWidth() - i) / 2;
			int k = this.getY() + this.getHeight() - 2;
			TabButtonWidget.fill(matrices, j, k, j + i, k + 1, color);
		}

		protected int getTextureV() {
			int i = 2;
			if (this.isCurrentTab()) {
				i -= 2;
			}
			if (this.isHovered()) {
				i += 1;
			}
			return i * 24;
		}

		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

		@Override
		public void playDownSound(SoundManager soundManager) {}

		public Tab getTab() {
			return this.tab;
		}

		public boolean isCurrentTab() {
			return this.tabManager.getCurrentTab() == this.tab;
		}
	}

	@Environment(value=EnvType.CLIENT)
	public static class TabNavigationWidget extends AbstractParentElement implements Drawable, Element, Selectable {
		public static final Identifier HEADER_SEPARATOR_TEXTURE = new Identifier("screenfx","header_separator.png");
		private final GridWidget grid;
		private final int tabNavWidth;
		private final TabManager tabManager;
		private final ImmutableList<Tab> tabs;
		private final ImmutableList<TabButtonWidget> tabButtons;

		TabNavigationWidget(int x, TabManager tabManager, Iterable<Tab> tabs) {
			this.tabNavWidth = x;
			this.tabManager = tabManager;
			this.tabs = ImmutableList.copyOf(tabs);
			this.grid = new GridWidget(0, 0);
			this.grid.getMainPositioner().alignHorizontalCenter();
			ImmutableList.Builder<TabButtonWidget> builder = ImmutableList.builder();
			int i = 0;
			for (Tab tab : tabs) {
				builder.add(this.grid.add(new TabButtonWidget(tabManager, tab, 0, 24), 0, i++));
			}
			this.tabButtons = builder.build();
		}

		public static Builder builder(TabManager tabManager, int width) {
			return new Builder(tabManager, width);
		}

		@Override
		public void setFocused(@Nullable Element focused) {
			super.setFocused(focused);
			if (focused instanceof TabButtonWidget tabButtonWidget) {
				this.tabManager.setCurrentTab(tabButtonWidget.getTab(), true);
			}
		}

		@Override
		public List<? extends Element> children() {
			return this.tabButtons;
		}

		@Override
		public Selectable.SelectionType getType() {
			return this.tabButtons.stream().map(ClickableWidget::getType).max(Comparator.naturalOrder()).orElse(Selectable.SelectionType.NONE);
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			TabNavigationWidget.fill(matrices, 0, 0, this.tabNavWidth, 24, -16777216);
			RenderSystem.setShaderTexture(0, HEADER_SEPARATOR_TEXTURE);
			TabNavigationWidget.drawTexture(matrices, 0, this.grid.getY() + this.grid.getHeight() - 2, 0.0f, 0.0f, this.tabNavWidth, 2, 32, 2);
			for (TabButtonWidget tabButtonWidget : this.tabButtons) {
				tabButtonWidget.render(matrices, mouseX, mouseY, delta);
			}
		}

		public void init() {
			int i = Math.min(400, this.tabNavWidth) - 28;
			for (TabButtonWidget tabButtonWidget : this.tabButtons) {
				tabButtonWidget.setWidth(MathHelper.roundUpToMultiple(i / this.tabs.size(), 2));
			}
			this.grid.recalculateDimensions();
			this.grid.setX(MathHelper.roundUpToMultiple((this.tabNavWidth - i) / 2, 2));
			this.grid.setY(0);
		}

		public void selectTab(int index, boolean clickSound) {
			if (this.getFocused() != null) {
				this.setFocused(this.tabButtons.get(index));
			} else {
				this.tabManager.setCurrentTab(this.tabs.get(index), clickSound);
			}
		}

		public boolean trySwitchTabsWithKey(int keyCode) {
			int i;
			if (Screen.hasControlDown() && (i = this.getTabForKey(keyCode)) != -1) {
				this.selectTab(MathHelper.clamp(i, 0, this.tabs.size() - 1), true);
				return true;
			}
			return false;
		}

		private int getTabForKey(int keyCode) {
			if (keyCode >= 49 && keyCode <= 57) {
				return keyCode - 49;
			}
			int i;
			if (keyCode == 258 && (i = this.getCurrentTabIndex()) != -1) {
				int j = Screen.hasShiftDown() ? i - 1 : i + 1;
				return Math.floorMod(j, this.tabs.size());
			}
			return -1;
		}

		private int getCurrentTabIndex() {
			return this.tabs.indexOf(this.tabManager.getCurrentTab());
		}

		@Environment(value=EnvType.CLIENT)
		public static class Builder {
			private final int width;
			private final TabManager tabManager;
			private final List<Tab> tabs = new ArrayList<>();

			Builder(TabManager tabManager, int width) {
				this.tabManager = tabManager;
				this.width = width;
			}

			public Builder tabs(Tab ... tabs) {
				Collections.addAll(this.tabs, tabs);
				return this;
			}

			public TabNavigationWidget build() {
				return new TabNavigationWidget(this.width, this.tabManager, this.tabs);
			}
		}
	}

	private static List<?> addDefaultMapEntry(EntryInfo info) {
		List<Object> valueList = new ArrayList<>(mapEntries.get(info.map).size() - 1);
		int counter = 1;
		for (EntryInfo mapInfo : mapEntries.get(info.map)) {
			if (mapInfo.mapPosition != 0) {
				valueList.add(mapEntries.get(info.map).get(counter).defaultValue);
				counter += 1;
			}
		}
		((Map<String, List<?>>) mapEntries.get(info.map).get(0).value).put("", valueList);
		return valueList;
	}

	private static void updateMapPositionEntries(EntryInfo info) {
		List<?> valueList = ((Map<String, List<?>>) info.value).get(((Map<String, ?>) info.value).keySet().stream().toList().get(info.index));
		if (valueList.size() > 0) {
			int counter = 0;
			for (EntryInfo mapInfo : mapEntries.get(info.map)) {
				if (mapInfo.mapPosition != 0) {
					mapInfo.value = valueList.get(counter);
					mapInfo.tempValue = mapInfo.value.toString();
					counter += 1;
				}
			}
		}
	}

	public static void init(String modid, Class<?> config) {
		path = FabricLoader.getInstance().getConfigDir().resolve(modid + ".json");
		configClass.put(modid, config);

		for (Field field : config.getFields()) {
			EntryInfo info = new EntryInfo();
			if ((field.isAnnotationPresent(Entry.class) || field.isAnnotationPresent(Comment.class)) && !field.isAnnotationPresent(Server.class) && !field.isAnnotationPresent(Hidden.class))
				if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) initClient(modid, field, info);
			if (field.isAnnotationPresent(Comment.class)) info.centered = field.getAnnotation(Comment.class).centered();
			if (field.isAnnotationPresent(Entry.class))
				try {
					info.defaultValue = field.get(null);
				} catch (IllegalAccessException ignored) {}
		}
		try { gson.fromJson(Files.newBufferedReader(path), config); }
		catch (Exception e) { write(modid); }

		for (EntryInfo info : entries) {
			if (info.field.isAnnotationPresent(Entry.class))
				try {
					info.value = info.field.get(null);
					info.tempValue = info.value.toString();
				} catch (IllegalAccessException ignored) {}
		}
	}
	@Environment(EnvType.CLIENT)
	private static void initClient(String modid, Field field, EntryInfo info) {
		Class<?> type = field.getType();
		Entry e = field.getAnnotation(Entry.class);
		info.width = e != null ? e.width() : 0;
		info.field = field;
		info.id = modid;
		info.map = e != null ? e.map() : "";
		info.mapPosition = e != null ? e.mapPosition() : -1;

		if (e != null) {
			if (info.mapPosition != -1) {
				if (!mapEntries.containsKey(info.map)) {
					mapEntries.put(info.map, new ArrayList<>());
				}
				mapEntries.get(info.map).add(info.mapPosition, info);
			}
			if (!e.name().equals("")) info.name = Text.translatable(e.name());
			if (type == int.class) textField(info, Integer::parseInt, INTEGER_ONLY, (int) e.min(), (int) e.max(), true);
			else if (type == float.class) textField(info, Float::parseFloat, DECIMAL_ONLY, (float) e.min(), (float) e.max(), false);
			else if (type == double.class) textField(info, Double::parseDouble, DECIMAL_ONLY, e.min(), e.max(), false);
			else if (type == String.class || type == List.class) {
				textField(info, String::length, null, Math.min(e.min(), 0), Math.max(e.max(), 1), true);
			} else if (type == Map.class) {
				textField(info, String::length, null, Math.min(e.min(), 0), Math.max(e.max(), 1), true);
			} else if (type == boolean.class) {
				Function<Object, Text> func = value -> Text.translatable((Boolean) value ? "gui.yes" : "gui.no").formatted((Boolean) value ? Formatting.GREEN : Formatting.RED);
				info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
					info.value = !(Boolean) info.value;
					button.setMessage(func.apply(info.value));
				}, func);
			} else if (type.isEnum()) {
				List<?> values = Arrays.asList(field.getType().getEnumConstants());
				Function<Object, Text> func = value -> Text.translatable(modid + ".enum." + type.getSimpleName() + "." + info.value.toString());
				info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
					int index = values.indexOf(info.value) + 1;
					info.value = values.get(index >= values.size() ? 0 : index);
					button.setMessage(func.apply(info.value));
				}, func);
			}
		}
		entries.add(info);
	}

	private static void textField(EntryInfo info, Function<String,Number> f, Pattern pattern, double min, double max, boolean cast) {
		boolean isNumber = pattern != null;
		info.widget = (BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) (t, b) -> s -> {
			s = s.trim();
			if (!(s.isEmpty() || !isNumber || pattern.matcher(s).matches())) return false;

			Number value = 0;
			boolean inLimits = false;
			info.error = null;
			if (!(isNumber && s.isEmpty()) && !s.equals("-") && !s.equals(".")) {
				try { value = f.apply(s); } catch(NumberFormatException e){ return false; }
				inLimits = value.doubleValue() >= min && value.doubleValue() <= max;
				info.error = inLimits? null : Text.literal(value.doubleValue() < min ?
						"§cMinimum " + (isNumber? "value" : "length") + (cast? " is " + (int)min : " is " + min) :
						"§cMaximum " + (isNumber? "value" : "length") + (cast? " is " + (int)max : " is " + max)).formatted(Formatting.RED);
			}

			info.tempValue = s;
			t.setEditableColor(inLimits? 0xFFFFFFFF : 0xFFFF7777);
			info.inLimits = inLimits;
			b.active = entries.stream().allMatch(e -> e.inLimits);

			if (inLimits && info.field.getType() == List.class) {
				if (((List<String>) info.value).size() == info.index) ((List<String>) info.value).add("");
				((List<String>) info.value).set(info.index, Arrays.stream(info.tempValue.replace("[", "").replace("]", "").split(", ")).toList().get(0));
			}
			else if (inLimits && info.field.getType() == Map.class) {
				List<?> valueList;
				if (((Map<String, ?>) mapEntries.get(info.map).get(0).value).size() != 0) {
					valueList = ((Map<String, List<?>>) info.value).get(((Map<String, ?>) info.value).keySet().stream().toList().get(info.index));
				}
				else {
					valueList = addDefaultMapEntry(info);
				}
				((Map<String, ?>) info.value).remove(((Map<String, ?>) info.value).keySet().stream().toList().get(info.index));
				if (((Map<String, ?>) mapEntries.get(info.map).get(0).value).containsKey(info.tempValue)) {
					((Map<String, List<?>>) info.value).put(info.tempValue + "_", valueList);
				}
				else {
					((Map<String, List<?>>) info.value).put(info.tempValue, valueList);
				}
			}
			else if (inLimits)
				info.value = isNumber? value : s;

			if (info.field.getAnnotation(Entry.class).isColor()) {
				if (!s.contains("#")) s = '#' + s;
				if (!HEXADECIMAL_ONLY.matcher(s).matches()) return false;
				try {
					info.colorButton.setMessage(Text.literal("⬛").setStyle(Style.EMPTY.withColor(Color.decode(info.tempValue).getRGB())));
				} catch (Exception ignored) {}
			}
			return true;
		};
	}

	public static void write(String modid) {
		path = FabricLoader.getInstance().getConfigDir().resolve(modid + ".json");
		try {
			if (!Files.exists(path)) Files.createFile(path);
			Files.write(path, gson.toJson(configClass.get(modid).getDeclaredConstructor().newInstance()).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Environment(EnvType.CLIENT)
	public static Screen getScreen(Screen parent, String modid) {
		return new MidnightConfigScreen(parent, modid);
	}
	@Environment(EnvType.CLIENT)
	public static class MidnightConfigScreen extends Screen {
		protected MidnightConfigScreen(Screen parent, String modid) {
			super(Text.translatable(modid + ".title"));
			this.parent = parent;
			this.modid = modid;
			this.translationPrefix = modid + ".";
		}
		public final String translationPrefix;
		public final Screen parent;
		public final String modid;
		public MidnightConfigListWidget list;
		public boolean reload = false;
		public TabManager tabManager = new TabManager(a -> {}, a -> {});
		public Tab prevTab;
		public TabNavigationWidget tabNavigation;
		public ButtonWidget done;

		// Real Time config update //
		@Override
		public void tick() {
			super.tick();
			if (prevTab != null && prevTab != tabManager.getCurrentTab()) {
				prevTab = tabManager.getCurrentTab();
				this.list.clear();
				fillList();
				list.setScrollAmount(0);
			}
			for (EntryInfo info : entries) {
				try {info.field.set(null, info.value);} catch (IllegalAccessException ignored) {}
			}
			updateResetButtons();
		}
		public void updateResetButtons() {
			if (this.list != null) {
				for (ButtonEntry entry : this.list.children()) {
					if (entry.buttons != null && entry.buttons.size() > 1 && entry.buttons.get(1) instanceof ButtonWidget button) {
						if (!Objects.equals(entry.info.value.toString(), entry.info.defaultValue.toString())) {
							button.setMessage(button.getMessage().copy().formatted(Formatting.WHITE));
						} else {
							button.setMessage(button.getMessage().copy().formatted(Formatting.GRAY));
						}
						button.active = !Objects.equals(entry.info.value.toString(), entry.info.defaultValue.toString());
					}
				}
			}
		}
		public void loadValues() {
			try { gson.fromJson(Files.newBufferedReader(path), configClass.get(modid)); }
			catch (Exception e) { write(modid); }

			for (EntryInfo info : entries) {
				if (info.field.isAnnotationPresent(Entry.class))
					try {
						info.value = info.field.get(null);
						info.tempValue = info.value.toString();
					} catch (IllegalAccessException ignored) {}
			}
		}
		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (this.tabNavigation.trySwitchTabsWithKey(keyCode)) return true;
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
		public Tooltip getTooltip(EntryInfo info) {
			return Tooltip.of(info.error != null ? info.error : I18n.hasTranslation(translationPrefix+info.field.getName()+".tooltip") ? Text.translatable(translationPrefix+info.field.getName()+".tooltip") : Text.empty());
		}
		public void refresh() {
			double scrollAmount = list.getScrollAmount();
			list.clear();
			fillList();
			list.setScrollAmount(scrollAmount);
		}
		@Override
		public void init() {
			super.init();
			loadValues();

			Map<String, Tab> tabs = new HashMap<>();
			for (EntryInfo e : entries) {
				if (e.id.equals(modid)) {
					String tabId = e.field.isAnnotationPresent(Entry.class) ? e.field.getAnnotation(Entry.class).category() : e.field.getAnnotation(Comment.class).category();
					String name = translationPrefix + "category." + tabId;
					if (!I18n.hasTranslation(name) && tabId.equals("default"))
						name = translationPrefix + "title";
					Tab tab = new Tab(Text.translatable(name));
					if (!tabs.containsKey(name)) {
						e.tab = tab;
						tabs.put(name, tab);
					} else e.tab = tabs.get(name);
				}
			}
			tabNavigation = TabNavigationWidget.builder(tabManager, this.width).tabs(tabs.values().toArray(new Tab[0])).build();
			if (tabs.size() > 1) this.addDrawableChild(tabNavigation);
			if (!reload) tabNavigation.selectTab(0, false);
			tabNavigation.init();
			prevTab = tabManager.getCurrentTab();

			this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
				loadValues();
				Objects.requireNonNull(client).setScreen(parent);
			}).dimensions(this.width / 2 - 154, this.height - 28, 150, 20).build());
			done = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
				for (EntryInfo info : entries)
					if (info.id.equals(modid)) {
						try {
							info.field.set(null, info.value);
						} catch (IllegalAccessException ignored) {}
					}
				write(modid);
				Objects.requireNonNull(client).setScreen(parent);
			}).dimensions(this.width / 2 + 4, this.height - 28, 150, 20).build());

			this.list = new MidnightConfigListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
			if (this.client != null && this.client.world != null) this.list.setRenderBackground(false);
			this.addSelectableChild(this.list);

			fillList();
		}
		public void fillList() {
			for (EntryInfo info : entries) {
				if (info.id.equals(modid) && (info.tab == null || info.tab == tabManager.getCurrentTab())) {
					Text name = Objects.requireNonNullElseGet(info.name, () -> Text.translatable(translationPrefix + info.field.getName()));
					ButtonWidget resetButton = ButtonWidget.builder(Text.literal("Reset").formatted(Formatting.GRAY), (button -> {
						info.value = info.defaultValue;
						info.tempValue = info.defaultValue.toString();
						info.index = 0;
						this.refresh();
					})).dimensions(width - 205, 0, 40, 20).build();

					if (info.mapPosition > 0) {
						EntryInfo relevantMapEntry = mapEntries.get(info.map).get(0);
						resetButton = ButtonWidget.builder(Text.literal("Reset").formatted(Formatting.GRAY), (button -> {
							info.value = info.defaultValue;
							info.tempValue = info.defaultValue.toString();
							info.index = 0;
							if (((Map<String, ?>) mapEntries.get(info.map).get(0).value).size() == 0) {
								addDefaultMapEntry(info);
							}
							((Map<String, List<Object>>) relevantMapEntry.value)
									.get(((Map<String, ?>) relevantMapEntry.value).keySet().stream().toList().get(relevantMapEntry.index))
									.set(info.mapPosition - 1, info.defaultValue);
							this.reload = true;
							this.refresh();
							this.reload = false;
						})).dimensions(width - 205, 0, 40, 20).build();
					}

					if (info.widget instanceof Map.Entry) {
						Map.Entry<ButtonWidget.PressAction, Function<Object, Text>> widget = (Map.Entry<ButtonWidget.PressAction, Function<Object, Text>>) info.widget;
						if (info.field.getType().isEnum())
							widget.setValue(value -> Text.translatable(translationPrefix + "enum." + info.field.getType().getSimpleName() + "." + info.value.toString()));
						this.list.addButton(List.of(ButtonWidget.builder(widget.getValue().apply(info.value), widget.getKey()).dimensions(width - 160, 0, 150, 20).tooltip(getTooltip(info)).build(), resetButton), name, info);
					} else if (info.field.getType() == List.class) {
						if (!reload) info.index = 0;
						TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, Text.empty());
						widget.setMaxLength(info.width);
						if (info.index < ((List<String>) info.value).size())
							widget.setText((String.valueOf(((List<String>) info.value).get(info.index))));
						Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) info.widget).apply(widget, done);
						widget.setTextPredicate(processor);
						resetButton.setWidth(20);
						resetButton.setMessage(Text.literal("R").formatted(Formatting.GRAY));
						ButtonWidget cycleButton = ButtonWidget.builder(Text.literal(String.valueOf(info.index)).formatted(Formatting.GOLD), (button -> {
							if (((List<?>) info.value).contains("")) ((List<String>) info.value).remove("");
							info.index = info.index + 1;
							if (info.index > ((List<String>) info.value).size()) info.index = 0;
							this.reload = true;
							this.refresh();
							this.reload = false;
						})).dimensions(width - 185, 0, 20, 20).build();
						widget.setTooltip(getTooltip(info));
						this.list.addButton(List.of(widget, resetButton, cycleButton), name, info);
					} else if (info.field.getType() == Map.class) {
						ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("Delete").formatted(Formatting.GRAY), (button -> {
							((Map<String, ?>) info.value).remove(((Map<String, ?>) info.value).keySet().stream().toList().get(info.index));
							info.tempValue = info.defaultValue.toString();
							info.index = Math.max(info.index - 1, 0);
							if (((Map<String, ?>) info.value).size() > 0) {
								updateMapPositionEntries(info);
							}
							this.reload = true;
							this.refresh();
							this.reload = false;
						})).dimensions(width - 205, 0, 40, 20).build();
						if (!reload) info.index = 0;
						TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, Text.empty());
						widget.setMaxLength(info.width);
						if (info.index < ((Map<String, ?>) info.value).size())
							widget.setText((String.valueOf(((Map<String, ?>) info.value).keySet().stream().toList().get(info.index))));
						Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) info.widget).apply(widget, done);
						widget.setTextPredicate(processor);
						deleteButton.setWidth(20);
						deleteButton.setMessage(Text.literal("D").formatted(Formatting.GRAY));
						ButtonWidget cycleButton = ButtonWidget.builder(Text.literal(String.valueOf(info.index)).formatted(Formatting.GOLD), (button -> {
							info.index += 1;
							if (info.index == ((Map<String, ?>) info.value).size() && !((Map<String, ?>) info.value).get(((Map<String, ?>) info.value).keySet().stream().toList().get(info.index - 1)).equals("item_name")) {
								addDefaultMapEntry(info);
							}
							if (info.index >= ((Map<String, ?>) info.value).size()) {
								info.index = 0;
							}
							if (((Map<String, ?>) info.value).size() != 0) {
								updateMapPositionEntries(info);
							}
							this.reload = true;
							this.refresh();
							this.reload = false;
						})).dimensions(width - 185, 0, 20, 20).build();
						widget.setTooltip(getTooltip(info));
						this.list.addButton(List.of(widget, deleteButton, cycleButton), name, info);
					} else if (info.widget != null) {
						ClickableWidget widget;
						Entry e = info.field.getAnnotation(Entry.class);
						if (e.isSlider())
							widget = new MidnightSliderWidget(width - 160, 0, 150, 20, Text.of(info.tempValue), (Double.parseDouble(info.tempValue) - e.min()) / (e.max() - e.min()), info);
						else
							widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, null, Text.of(info.tempValue));
						if (widget instanceof TextFieldWidget textField) {
							textField.setMaxLength(info.width);
							textField.setText(info.tempValue);
							Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) info.widget).apply(textField, done);
							textField.setTextPredicate(processor);
						}
						widget.setTooltip(getTooltip(info));
						if (e.isColor()) {
							resetButton.setWidth(20);
							resetButton.setMessage(Text.literal("R").formatted(Formatting.GRAY));
							ButtonWidget colorButton = ButtonWidget.builder(Text.literal("⬛"), (button -> {
							})).dimensions(width - 185, 0, 20, 20).build();
							try {
								colorButton.setMessage(Text.literal("⬛").setStyle(Style.EMPTY.withColor(Color.decode(info.tempValue).getRGB())));
							} catch (Exception ignored) {
							}
							info.colorButton = colorButton;
							colorButton.active = false;
							this.list.addButton(List.of(widget, resetButton, colorButton), name, info);
						} else this.list.addButton(List.of(widget, resetButton), name, info);
					} else {
						this.list.addButton(List.of(), name, info);
					}
				}
				updateResetButtons();
			}
		}

		@Override
		public void renderBackground(MatrixStack matrices, int vOffset) {
			assert this.client != null;
			if (this.client.world != null) {
				fillGradient(matrices, 0, 0, this.width, this.height, 2013265920, -2113929216);
			} else {
				this.renderBackgroundTexture(vOffset);
			}
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			this.renderBackground(matrices);
			this.list.render(matrices, mouseX, mouseY, delta);

			drawCenteredTextWithShadow(matrices, textRenderer, title.asOrderedText(), width / 2, 15, 0xFFFFFF);
			super.render(matrices,mouseX,mouseY,delta);
		}
	}
	@Environment(EnvType.CLIENT)
	public static class MidnightConfigListWidget extends ElementListWidget<ButtonEntry> {
		TextRenderer textRenderer;

		public MidnightConfigListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
			super(minecraftClient, i, j, k, l, m);
			this.centerListVertically = false;
			textRenderer = minecraftClient.textRenderer;
		}
		@Override
		public int getScrollbarPositionX() { return this.width -7; }

		public void addButton(List<ClickableWidget> buttons, Text text, EntryInfo info) {
			this.addEntry(new ButtonEntry(buttons, text, info));
		}
		public void clear() {
			this.clearEntries();
		}
		@Override
		public int getRowWidth() { return 10000; }
	}
	public static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
		private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		public final List<ClickableWidget> buttons;
		private final Text text;
		public final EntryInfo info;
		private final List<ClickableWidget> children = new ArrayList<>();
		public static final Map<ClickableWidget, Text> buttonsWithText = new HashMap<>();

		private ButtonEntry(List<ClickableWidget> buttons, Text text, EntryInfo info) {
			if (!buttons.isEmpty()) buttonsWithText.put(buttons.get(0),text);
			this.buttons = buttons;
			this.text = text;
			this.info = info;
			children.addAll(buttons);
		}
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			buttons.forEach(b -> { b.setY(y); b.render(matrices, mouseX, mouseY, tickDelta); });
			if (text != null && (!text.getString().contains("spacer") || !buttons.isEmpty())) {
				if (info.centered) textRenderer.drawWithShadow(matrices, text, MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f - (textRenderer.getWidth(text) / 2f), y + 5, 0xFFFFFF);
				else DrawableHelper.drawTextWithShadow(matrices, textRenderer, text, 12, y + 5, 0xFFFFFF);
			}
		}
		public List<? extends Element> children() {return children;}
		public List<? extends Selectable> selectableChildren() {return children;}
	}
	private static class MidnightSliderWidget extends SliderWidget {
		private final EntryInfo info; private final Entry e;
		public MidnightSliderWidget(int x, int y, int width, int height, Text text, double value, EntryInfo info) {
			super(x, y, width, height, text, value);
			this.e = info.field.getAnnotation(Entry.class);
			this.info = info;
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.of(info.tempValue));
		}

		@Override
		protected void applyValue() {
			if (info.field.getType() == int.class) info.value = ((Number) (e.min() + value * (e.max() - e.min()))).intValue();
			else if (info.field.getType() == double.class) info.value = Math.round((e.min() + value * (e.max() - e.min())) * (double) e.precision()) / (double) e.precision();
			else if (info.field.getType() == float.class) info.value = Math.round((e.min() + value * (e.max() - e.min())) * (float) e.precision()) / (float) e.precision();
			info.tempValue = String.valueOf(info.value);
			if (info.mapPosition > 0) {
				if (((Map<String, ?>) mapEntries.get(info.map).get(0).value).size() == 0) {
					addDefaultMapEntry(info);
				}
				EntryInfo relevantMapEntry = mapEntries.get(info.map).get(0);
				((Map<String, List<Object>>) relevantMapEntry.value)
						.get(((Map<String, ?>) relevantMapEntry.value).keySet().stream().toList().get(relevantMapEntry.index))
						.set(info.mapPosition - 1, info.value);
			}
		}
	}
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Entry {
		int width() default 100;
		double min() default Double.MIN_NORMAL;
		double max() default Double.MAX_VALUE;
		String name() default "";
		boolean isColor() default false;
		boolean isSlider() default false;
		int precision() default 100;
		String category() default "default";
		String map() default "";
		int mapPosition() default -1;
	}
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Client {}
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Server {}
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Hidden {}
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Comment {
		boolean centered() default false;
		String category() default "default";
	}

	public static class HiddenAnnotationExclusionStrategy implements ExclusionStrategy {
		public boolean shouldSkipClass(Class<?> clazz) { return false; }
		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			return fieldAttributes.getAnnotation(Entry.class) == null;
		}
	}
}