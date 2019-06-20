(ns id.nadiar.colorscheme.views
  (:require
    [re-frame.core :as re-frame]
    [id.nadiar.colorscheme.subs :as subs]
    ))

(defn ensure-color-number [number]
  (let [maybe-number (js/parseInt number)
        nan? (js/isNaN maybe-number)]
    (if nan? 0 maybe-number)))

(defn ->rgb [red green blue]
  (let [format-color* (fn [number]
                        (let [number-str (str number)
                              count-str (count number-str)]
                          (case count-str
                            1 (str "00" number-str)
                            2 (str "0" number-str)
                            number-str)))
        format-color (comp format-color* ensure-color-number)]
    (str "rgb(" (format-color red) "," (format-color green) "," (format-color blue) ")")))

(defn rgb-str->rgb-data [rgb-str]
  (->> (->> (re-seq #"\d+" rgb-str)
            (partition-all 3))
       first
       (map js/parseInt)))

(defn ->in-range-hex-color [rgb-number]
  (let [hex (.toString (ensure-color-number rgb-number) 16)]
    (if (= 1 (count hex))
      (str "0" hex) hex)))

(defn rgb->hex [red green blue]
  (apply str "#" (map ->in-range-hex-color [red green blue])))

(defn calculate-complementary [red green blue]
  [(- 255 red) (- 255 green) (- 255 blue)])

(defn Slider [{:keys [onChange value] :as params
               :or   {value 0 onChange (fn [])}}]
  [:div {}
   [:input {:type     "range"
            :min      0
            :max      255
            :value    value
            :onChange onChange}]
   [:h3 value]])

(defn Reactangle [{:keys [color]}]
  [:div {:className "ba b--light-silver"
         :style     {:width 100 :height 100 :backgroundColor color}}])

(defn ColorSlider [{:keys [color onChange value title]}]
  [:div {:className "flex flex-column pa3 ma3 tc ba b--light-silver"}
   [:h3 title]
   [Reactangle {:color color}]
   [Slider {:onChange onChange :value value}]
   [:span color]
   [:span (apply rgb->hex (rgb-str->rgb-data color))]])

(defn ColorBox [{:keys [color rgb hex title]}]
  [:div {:className "flex flex-column pa3 ma3 tc ba b--light-silver"}
   [:h3 title]
   [Reactangle {:color color}]
   [:span rgb]
   [:span hex]])

(defn RenderComplementary [{:keys [red green blue]}]
  (let [complementary-data (calculate-complementary red green blue)
        complementary-rgb (apply ->rgb complementary-data)]
    [ColorBox {:title "Complementary"
               :color complementary-rgb
               :rgb   complementary-rgb
               :hex   (apply rgb->hex complementary-data)}]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        db (re-frame/subscribe [:_DB_])]
    (fn []
      (let [[red green blue :as rgb] (or (seq (map #(get @db %) [:red :green :blue])) [0 0 0])
            rgb-str (apply ->rgb rgb)
            hex (apply rgb->hex rgb)]
        [:div
         [:h1 "Hello from " @name]
         [:div {:className "flex flex-row"}
          [ColorSlider {:title    "RED"
                        :color    (->rgb red 0 0)
                        :onChange #(re-frame/dispatch [:register-data :red (-> % .-target .-value)])
                        :value    red}]
          [ColorSlider {:title    "GREEN"
                        :color    (->rgb 0 green 0)
                        :onChange #(re-frame/dispatch [:register-data :green (-> % .-target .-value)])
                        :value    green}]
          [ColorSlider {:title    "BLUE"
                        :color    (->rgb 0 0 blue)
                        :onChange #(re-frame/dispatch [:register-data :blue (-> % .-target .-value)])
                        :value    blue}]
          [ColorBox {:title "Combine"
                     :color rgb-str
                     :rgb rgb-str
                     :hex hex}]
          [RenderComplementary {:red red :green green :blue blue}]]
         ]))))
